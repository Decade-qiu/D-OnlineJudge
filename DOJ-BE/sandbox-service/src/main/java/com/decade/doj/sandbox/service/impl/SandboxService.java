package com.decade.doj.sandbox.service.impl;

import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.service.ISandboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SandboxService implements ISandboxService {

    /**
     * 通过占位符数量构造最终执行命令，例如：
     *   rawCmd = "python3 %s.py"，baseName = "Main" -> "python3 Main.py"
     *   rawCmd = "g++ %s.cpp -o %s.out && ./%s.out" -> 3 个占位符，将 baseName 填充三次
     *
     * @param rawCmd  带有若干 "%s" 占位符的 Shell 命令模板
     * @param baseName 去掉后缀后的文件名
     * @return 填充好占位符的最终命令字符串
     */
    public static String buildRunCommand(String rawCmd, String baseName) {
        // 计算 "%s" 出现的次数
        int placeholderCount = rawCmd.split("%s", -1).length - 1;
        return switch (placeholderCount) {
            case 1 -> String.format(rawCmd, baseName);
            case 2 -> String.format(rawCmd, baseName, baseName);
            case 3 -> String.format(rawCmd, baseName, baseName, baseName);
            default -> throw new IllegalArgumentException("Unsupported placeholder count in runCmd: " + placeholderCount);
        };
    }

    // 正则编译为静态常量，避免每次重复编译
    private static final Pattern MEM_PATTERN = Pattern.compile(
            "Maximum resident set size \\(kbytes\\): (\\d+)"
    );
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "Elapsed \\(wall clock\\) time \\(h:mm:ss or m:ss\\): \\d+:(\\d+\\.\\d+)"
    );
    // 构造 /usr/bin/time 与 timeout 的命令模板
    private static final String TIMEOUT_TEMPLATE = "/usr/bin/time -v timeout %ds %s";
    // 挂载目录
    private static final String MOUNT_PATH = "/app";

    @Override
    @Async("RunCodeThreadPool")
    public CompletableFuture<ExecuteMessage> runCodeInSandbox(String filePath, String filename, String lang) {
        ExecuteMessage result = _runCodeInSandbox(filePath, filename, lang);
        return CompletableFuture.completedFuture(result);
    }

    private ExecuteMessage _runCodeInSandbox(String filePath, String filename, String lang) {

        LanguageEnum languageEnum = LanguageEnum.getLanguageEnum(lang);

        // 镜像名称
        String imageName = languageEnum.getImageName();
        // 运行时挂载目录（宿主与容器）
        String fileDir = new File(filePath).getParent();
        String mountPath = MOUNT_PATH;

        int dotIndex = filename.lastIndexOf(".");
        String baseName = (dotIndex >= 0)
                ? filename.substring(0, dotIndex)
                : filename;

        try {
            // 构造真实要执行的命令
            String runCmd = buildRunCommand(languageEnum.getRunCmd(), baseName);
            String execCmd = String.format(
                    TIMEOUT_TEMPLATE,
                    languageEnum.getTimeLimit(),
                    runCmd
            );

            // 准备 Docker 运行命令列表
            List<String> command = Arrays.asList(
                    "docker", "run", "--rm",
                    "-v", fileDir + ":" + mountPath,
                    "--memory", languageEnum.getMemoryLimit(),
                    imageName,
                    execCmd
            );

            ProcessBuilder builder = new ProcessBuilder(command);
            // 合并标准输出与标准错误
            builder.redirectErrorStream(true);

            long startTimeMillis = System.currentTimeMillis();

            Process process = builder.start();

            // 读取进程输出
            StringBuilder fullOutputBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fullOutputBuilder.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            long endTimeMillis = System.currentTimeMillis();

            String fullOutput = fullOutputBuilder.toString().trim();

            // 匹配内存、耗时信息
            Matcher memMatcher = MEM_PATTERN.matcher(fullOutput);
            Matcher timeMatcher = TIME_PATTERN.matcher(fullOutput);

            String memoryUsage = memMatcher.find() ? memMatcher.group(1) : "-1";
            String timeUsed = timeMatcher.find()
                    ? timeMatcher.group(1)
                    : String.valueOf((endTimeMillis - startTimeMillis) / 1000.0);

            int statIndex = fullOutput.indexOf("Command being timed:");
            String trimmedOutput = (statIndex != -1)
                    ? fullOutput.substring(0, statIndex).trim()
                    : fullOutput;

            log.info("Exit code: {}", exitCode);
            log.info("Raw output:\n{}", fullOutput);

            return new ExecuteMessage()
                    .setExitValue(exitCode)
                    .setStatus(ExecuteMessage.getStatus(exitCode))
                    .setMessage(ExecuteMessage.show(exitCode) ? trimmedOutput : "")
                    .setTime(Double.parseDouble(timeUsed))
                    .setMemory(Long.parseLong(memoryUsage));
        } catch (Exception e) {
            log.error("运行沙箱代码出错", e);
            return new ExecuteMessage()
                    .setExitValue(1)
                    .setStatus("Runtime Error")
                    .setMessage("执行代码时发生错误: " + e.getMessage());
        }
    }

    @Override
    @Async("RunCodeThreadPool")
    public CompletableFuture<ExecuteMessage> runCodeInSandboxWI(String localPath, String inputname, String filename, String lang) {
        ExecuteMessage result = _runCodeInSandboxWI(localPath, inputname, filename, lang, null);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    @Async("ValidateThreadPool")
    public CompletableFuture<ExecuteMessage> runCodeInSandboxWIV(String localPath, String inputname, String output, String filename, String lang) {
        ExecuteMessage result = _runCodeInSandboxWI(localPath, inputname, filename, lang, output);
        return CompletableFuture.completedFuture(result);
    }

    private ExecuteMessage _runCodeInSandboxWI(String filePath, String inputname, String filename, String lang, String answer) {

        LanguageEnum languageEnum = LanguageEnum.getLanguageEnum(lang);

        // 镜像名称
        String imageName = languageEnum.getImageName();
        // 运行时挂载目录（宿主与容器）
        String fileDir = new File(filePath).getParent();
        String mountPath = MOUNT_PATH;

        int dotIndex = filename.lastIndexOf(".");
        String baseName = (dotIndex >= 0)
                ? filename.substring(0, dotIndex)
                : filename;

        try {
            // 构造真实要执行的命令
            String runCmd = buildRunCommand(languageEnum.getRunCmd(), baseName);
            runCmd += " < " + inputname; // 添加输入重定向
            String execCmd = String.format(
                    TIMEOUT_TEMPLATE,
                    languageEnum.getTimeLimit(),
                    runCmd
            );

            // 准备 Docker 运行命令列表
            List<String> command = Arrays.asList(
                    "docker", "run", "--rm",
                    "-v", fileDir + ":" + mountPath,
                    "--memory", languageEnum.getMemoryLimit(),
                    imageName,
                    execCmd
            );

            ProcessBuilder builder = new ProcessBuilder(command);
            // 合并标准输出与标准错误
            builder.redirectErrorStream(true);

            long startTimeMillis = System.currentTimeMillis();

            Process process = builder.start();

            // 读取进程输出
            StringBuilder fullOutputBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fullOutputBuilder.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            long endTimeMillis = System.currentTimeMillis();

            String fullOutput = fullOutputBuilder.toString().trim();

            // 匹配内存、耗时信息
            Matcher memMatcher = MEM_PATTERN.matcher(fullOutput);
            Matcher timeMatcher = TIME_PATTERN.matcher(fullOutput);

            String memoryUsage = memMatcher.find() ? memMatcher.group(1) : "-1";
            String timeUsed = timeMatcher.find()
                    ? timeMatcher.group(1)
                    : String.valueOf((endTimeMillis - startTimeMillis) / 1000.0);

            int statIndex = fullOutput.indexOf("Command being timed:");
            String trimmedOutput = (statIndex != -1)
                    ? fullOutput.substring(0, statIndex).trim()
                    : fullOutput;

            log.info("Exit code: {}", exitCode);
            log.info("Raw output:\n{}", fullOutput);

            String status = ExecuteMessage.getStatus(exitCode);
            if (answer != null && ExecuteMessage.getStatus(exitCode).equals("Finished")) {
                // 比较输出与答案
                boolean isCorrect = trimmedOutput.equals(answer.trim());
                if (isCorrect) {
                    status = "Accepted";
                    exitCode = 10;
                } else {
                    status = "Wrong Answer";
                    exitCode = 11;
                    trimmedOutput += "\n```\nExpected: " + answer.trim();
                }
            }

            return new ExecuteMessage()
                    .setExitValue(exitCode)
                    .setStatus(status)
                    .setMessage(ExecuteMessage.show(exitCode) ? trimmedOutput : "")
                    .setTime(Double.parseDouble(timeUsed))
                    .setMemory(Long.parseLong(memoryUsage));
        } catch (Exception e) {
            log.error("运行沙箱代码出错", e);
            return new ExecuteMessage()
                    .setExitValue(1)
                    .setStatus("Runtime Error")
                    .setMessage("执行代码时发生错误: " + e.getMessage());
        }
    }
}
