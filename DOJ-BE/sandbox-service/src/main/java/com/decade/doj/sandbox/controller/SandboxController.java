package com.decade.doj.sandbox.controller;

import com.decade.doj.common.client.ProblemClient;
import com.decade.doj.common.config.properties.ResourceProperties;
import com.decade.doj.common.domain.R;
import com.decade.doj.common.domain.po.Problem;
import com.decade.doj.common.utils.UserContext;
import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.service.ISandboxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@Api(tags = "沙箱相关接口")
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(ResourceProperties.class)
public class SandboxController {

    private final ResourceProperties resourceProperties;

    private final ISandboxService sandboxService;

    private final ProblemClient problemClient;

    @PostMapping("/code")
    @ApiOperation("运行代码文件")
    public CompletableFuture<R<ExecuteMessage>> runCode(@RequestParam("file") MultipartFile file, @RequestParam("language") @NotBlank String lang) throws IOException {
        if (file.isEmpty()) {
            return CompletableFuture.completedFuture(R.error("上传的文件不能为空!"));
        }

        if (LanguageEnum.isInValidLanguage(lang)) {
            return CompletableFuture.completedFuture(R.error("不支持的编程语言: " + lang));
        }

        String path = saveFile(file, resourceProperties.getCodePath(), null)[0];

        return sandboxService
                .runCodeInSandbox(path, file.getOriginalFilename(), lang)
                .thenApply(R::ok);
    }

    @PostMapping("/problem")
    @ApiOperation("运行题目代码")
    public CompletableFuture<R<ExecuteMessage>> runProblem(@RequestParam("file") MultipartFile file, @RequestParam("input") MultipartFile input, @RequestParam("language") @NotBlank String lang, @RequestParam("pid") Long pid) throws IOException {
        if (file.isEmpty()) {
            return CompletableFuture.completedFuture(R.error("上传的文件不能为空!"));
        }

        if (LanguageEnum.isInValidLanguage(lang)) {
            return CompletableFuture.completedFuture(R.error("不支持的编程语言: " + lang));
        }

        String[] Paths = saveFile(file, resourceProperties.getCodePath(), null);
        String codePath = Paths[0];
        saveFile(input, resourceProperties.getCodePath(), Paths[1]);

        return sandboxService
                .runCodeInSandboxWI(codePath, input.getOriginalFilename(), file.getOriginalFilename(), lang)
                .thenApply(R::ok);
    }

    @PostMapping("/validate")
    @ApiOperation("验证题目代码")
    public CompletableFuture<R<ExecuteMessage>> runProblemValidate(@RequestParam("file") MultipartFile file, @RequestParam("language") @NotBlank String lang, @RequestParam("pid") Long pid) throws IOException {
        if (file.isEmpty()) {
            return CompletableFuture.completedFuture(R.error("上传的文件不能为空!"));
        }

        if (LanguageEnum.isInValidLanguage(lang)) {
            return CompletableFuture.completedFuture(R.error("不支持的编程语言: " + lang));
        }

        String[] Paths = saveFile(file, resourceProperties.getCodePath(), null);
        String codePath = Paths[0];
        String[] data = saveText2File(pid, resourceProperties.getCodePath(), Paths[1]);

        return sandboxService
                .runCodeInSandboxWIV(codePath, data[0], data[1], file.getOriginalFilename(), lang, pid, Paths[2], UserContext.getCurrentUser())
                .thenApply(R::ok);
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
