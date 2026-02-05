package com.decade.doj.sandbox.controller;

import com.alibaba.fastjson.JSON;
import com.decade.doj.common.client.ProblemClient;
import com.decade.doj.common.client.SubmissionClient;
import com.decade.doj.common.config.properties.ResourceProperties;
import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.po.Problem;
import com.decade.doj.common.domain.po.Submission;
import com.decade.doj.common.utils.UserContext;
import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.domain.vo.JudgingTask;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.service.ISandboxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/sandbox")
@Tag(name = "沙箱相关接口")
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(ResourceProperties.class)
public class SandboxController {

    private final ResourceProperties resourceProperties;

    private final ISandboxService sandboxService;
    private final SubmissionClient submissionClient;
    private final ProblemClient problemClient;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/code")
    @Operation(summary = "运行代码文件(异步执行)")
    public CompletableFuture<R<ExecuteMessage>> runCode(@RequestParam("file") MultipartFile file, @RequestParam("language") @NotBlank String lang) {
        if (file.isEmpty()) {
            return CompletableFuture.completedFuture(R.error("上传的文件不能为空!"));
        }

        if (LanguageEnum.isInValidLanguage(lang)) {
            return CompletableFuture.completedFuture(R.error("不支持的编程语言: " + lang));
        }

        try {
            String path = saveFile(file, resourceProperties.getCodePath(), null)[0];
            return sandboxService
                    .runCodeInSandbox(path, file.getOriginalFilename(), lang)
                    .thenApply(R::ok);
        } catch (IOException e) {
            log.error("保存代码文件失败", e);
            return CompletableFuture.completedFuture(R.error("文件保存失败: " + e.getMessage()));
        }
    }

    @PostMapping("/problem")
    @Operation(summary = "运行题目代码(异步执行)")
    public CompletableFuture<R<ExecuteMessage>> runProblem(@RequestParam("file") MultipartFile file, @RequestParam("input") MultipartFile input, @RequestParam("language") @NotBlank String lang, @RequestParam("pid") Long pid) {
        if (file.isEmpty()) {
            return CompletableFuture.completedFuture(R.error("上传的文件不能为空!"));
        }

        if (LanguageEnum.isInValidLanguage(lang)) {
            return CompletableFuture.completedFuture(R.error("不支持的编程语言: " + lang));
        }

        try {
            String[] Paths = saveFile(file, resourceProperties.getCodePath(), null);
            String codePath = Paths[0];
            saveFile(input, resourceProperties.getCodePath(), Paths[1]);

            return sandboxService
                    .runCodeInSandboxWI(codePath, input.getOriginalFilename(), file.getOriginalFilename(), lang)
                    .thenApply(R::ok);
        } catch (IOException e) {
            log.error("保存代码/输入文件失败", e);
            return CompletableFuture.completedFuture(R.error("文件保存失败: " + e.getMessage()));
        }
    }

    @PostMapping("/validate")
    @Operation(summary = "验证题目代码(异步提交)")
    public R<Long> runProblemValidate(@RequestParam("file") MultipartFile file, @RequestParam("language") @NotBlank String lang, @RequestParam("pid") Long pid) throws IOException {
        if (file.isEmpty()) {
            return R.error(400, "上传的文件不能为空!");
        }

        if (LanguageEnum.isInValidLanguage(lang)) {
            return R.error(400, "不支持的编程语言: " + lang);
        }

        String[] Paths = saveFile(file, resourceProperties.getCodePath(), null);
        String codePath = Paths[0];
        String[] data = saveText2File(pid, resourceProperties.getCodePath(), Paths[1]);

        // 1. 调用 submission-service 创建一个 PENDING 状态的提交记录
        Submission submissionDTO = new Submission();
        submissionDTO.setProblemId(pid);
        submissionDTO.setUserId(UserContext.getCurrentUser());
        submissionDTO.setLanguage(lang);
        submissionDTO.setCode(Paths[2]);
        R<Long> response = submissionClient.submit(submissionDTO);
        if (!response.success()) {
            return R.error(500, "创建提交记录失败: " + response.getMsg());
        }
        Long submissionId = response.getData();

        // 2. 创建判题任务
        JudgingTask task = new JudgingTask()
            .setSubmissionId(submissionId)
            .setLocalPath(codePath)
            .setInput(data[0])
            .setOutput(data[1])
            .setFilename(file.getOriginalFilename())
            .setLang(lang)
            .setProblemId(pid)
            .setUid(UserContext.getCurrentUser());

        // 3. 将任务推入 Redis 队列
        redisTemplate.opsForList().leftPush("judging:queue", JSON.toJSONString(task));
        log.info("新的[验证]任务已推入队列, submissionId: {}", submissionId);

        // 4. 立即返回 submissionId
        return R.ok(submissionId);
    }

    private String[] saveText2File(Long pid, String basePath, String folderName) throws IOException {
        if (folderName == null) {
            folderName = UUID.randomUUID().toString();
        }

        String subFolderPathStr = basePath + folderName + FileSystems.getDefault().getSeparator();

        Path subFolderPath = Paths.get(subFolderPathStr);
        Files.createDirectories(subFolderPath);

        String inputFileName = pid + "_p_input.txt";
        // 从其他微服务读取input和output数据
        Problem problem = problemClient.getProblemById(pid).getData();
        String inputdata = problem.getTestData();
        String outputdata = problem.getTestAns();

        Path inputFilePath = subFolderPath.resolve(inputFileName);
        Files.writeString(inputFilePath, inputdata);

        return new String[]{inputFileName, outputdata};
    }

    private String[] saveFile(MultipartFile file, String basePath, String folderName) throws IOException {
        if (folderName == null) {
            folderName = UUID.randomUUID().toString();
        }

        String subFolderPathStr = basePath + folderName + FileSystems.getDefault().getSeparator();

        Path subFolderPath = Paths.get(subFolderPathStr);
        Files.createDirectories(subFolderPath);

        String origFilename = file.getOriginalFilename();
        if (origFilename == null || origFilename.isBlank()) {
            throw new IOException("上传文件原始文件名为空，无法保存");
        }

        Path destinationFilePath = subFolderPath.resolve(origFilename);

        byte[] bytes = file.getBytes();
        String content = new String(bytes, StandardCharsets.UTF_8);
        Files.write(destinationFilePath, bytes);
        // file.transferTo(destinationFilePath.toFile());

        return new String[]{destinationFilePath.toUri().getPath(), folderName, content};
    }

}
