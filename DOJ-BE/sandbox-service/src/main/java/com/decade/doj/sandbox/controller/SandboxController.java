package com.decade.doj.sandbox.controller;

import com.decade.doj.common.config.properties.ResourceProperties;
import com.decade.doj.common.domain.R;
import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.service.ISandboxService;
import com.decade.doj.sandbox.service.impl.SandboxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
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

    @PostMapping("/code")
    @ApiOperation("运行代码文件")
    public CompletableFuture<R<ExecuteMessage>> runCode(@RequestParam("file") MultipartFile file, @RequestParam("language") @NotBlank String lang) throws IOException {
        if (file.isEmpty()) {
            return CompletableFuture.completedFuture(R.error("上传的文件不能为空!"));
        }

        if (LanguageEnum.isInValidLanguage(lang)) {
            return CompletableFuture.completedFuture(R.error("不支持的编程语言: " + lang));
        }

        String path = saveCodeFile(file, resourceProperties.getCodePath());

        return sandboxService
                .runCodeInSandbox(path, file.getOriginalFilename(), lang)
                .thenApply(R::ok);
    }

    // @PostMapping("/problem")
    // @ApiOperation("测评")
    // public R<ExecuteMessage> validate(@NotNull String pid, @RequestParam("file") MultipartFile file, @RequestParam("language") @NotBlank String lang) throws IOException {
    //     return;
    // }

    private String saveCodeFile(MultipartFile file, String baseCodePath) throws IOException {
        String folderName = UUID.randomUUID().toString();

        String subFolderPathStr = baseCodePath + folderName + FileSystems.getDefault().getSeparator();

        Path subFolderPath = Paths.get(subFolderPathStr);
        Files.createDirectories(subFolderPath);

        String origFilename = file.getOriginalFilename();
        if (origFilename == null || origFilename.isBlank()) {
            throw new IOException("上传文件原始文件名为空，无法保存");
        }

        Path destinationFilePath = subFolderPath.resolve(origFilename);

        file.transferTo(destinationFilePath.toFile());

        return destinationFilePath.toUri().getPath();
    }

}
