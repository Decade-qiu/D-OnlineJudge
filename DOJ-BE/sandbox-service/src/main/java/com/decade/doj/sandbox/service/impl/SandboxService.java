package com.decade.doj.sandbox.service.impl;

import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.service.ISandboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SandboxService implements ISandboxService {

    // private final RunCodeWithoutInput runCodeWithoutInput;
    //
    // private final RunCodeWithInput runCodeWithInput;
    //
    // private final RemoteProperty remoteProperty;

    private String getImageName(String lang) {
        return switch (lang.toLowerCase()) {
            case "python" -> "code-runner-python";
            case "java" -> "code-runner-java";
            // 可扩展更多语言
            default -> throw new IllegalArgumentException("不支持的语言: " + lang);
        };
    }

    @Override
    public ExecuteMessage runCodeInSandbox(String filePath, String filename, String lang) {
        LanguageEnum languageEnum = LanguageEnum.getLanguageEnum(lang);
        String imageName = getImageName(lang);
        String fileDir = new File(filePath).getParent();
        String mountPath = "/app";

        try {
            String execCmd = String.format(
                    "/usr/bin/time -v timeout %ds %s %s",
                    languageEnum.getTimeLimit(),
                    languageEnum.getRunCmd(),
                    "Main." + languageEnum.getExtension()
            );

            List<String> command = Arrays.asList(
                    "docker", "run", "--rm",
                    "-v", fileDir + ":" + mountPath,
                    "--memory", languageEnum.getMemoryLimit(),
                    imageName,
                    execCmd
            );

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);

            long startTime = System.currentTimeMillis();

            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            long endTime = System.currentTimeMillis();

            Pattern memPattern = Pattern.compile("Maximum resident set size \\(kbytes\\): (\\d+)");
            Pattern timePattern = Pattern.compile("Elapsed \\(wall clock\\) time \\(h:mm:ss or m:ss\\): \\d+:(\\d+\\.\\d+)");

            Matcher memMatcher = memPattern.matcher(output.toString());
            Matcher timeMatcher = timePattern.matcher(output.toString());

            String memoryUsage = memMatcher.find() ? memMatcher.group(1) : "-1";
            String timeUsed = timeMatcher.find() ? timeMatcher.group(1) : String.valueOf((endTime - startTime));

            String rawOutput = output.toString().trim();
            int statIndex = rawOutput.indexOf("Command being timed:");
            String displayOutput;
            if (statIndex != -1) {
                displayOutput = rawOutput.substring(0, statIndex).trim();
            } else {
                displayOutput = rawOutput;
            }

            return new ExecuteMessage()
                    .setExitValue(exitCode)
                    .setStatus(ExecuteMessage.getStatus(exitCode))
                    .setMessage(displayOutput.trim())
                    .setTime(Double.parseDouble(timeUsed))
                    .setMemory(Long.parseLong(memoryUsage.replace(" KB", "")));

        } catch (Exception e) {
            return new ExecuteMessage()
                    .setExitValue(1)
                    .setStatus("Runtime Error")
                    .setMessage("执行代码时发生错误: " + e.getMessage());
        }
    }

    @Override
    public ExecuteMessage runProblemCodeInSandbox(String localPath, String filename, String lang, String pid) {
        // SFTPUtil.uploadFile(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), localPath, remoteProperty.getScriptPath()+"/problem/"+pid);
        // LanguageEnum languageEnum = LanguageEnum.getLanguageEnum(lang);
        // if (languageEnum == null) {
        //     return new ExecuteMessage()
        //             .setMessage("不支持的语言!");
        // }
        // return runCodeWithInput.run(languageEnum, filename, pid);
        return new ExecuteMessage();
    }

}
