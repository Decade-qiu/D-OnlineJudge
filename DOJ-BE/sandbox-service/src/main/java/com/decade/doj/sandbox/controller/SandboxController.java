package com.decade.doj.sandbox.controller;

import com.decade.doj.common.config.properties.ResourceProperties;
import com.decade.doj.common.domain.R;
import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.service.impl.SandboxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/sandbox")
@Api(tags = "沙箱相关接口")
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(ResourceProperties.class)
public class SandboxController {

    private final ResourceProperties resourceProperties;

    private final SandboxService sandboxService;

    @PostMapping("/code")
    @ApiOperation("运行代码文件")
    public R<ExecuteMessage> runCode(@RequestParam("file") MultipartFile file, @RequestParam("language") @NotBlank String lang) {
        if (file.isEmpty()) {
            return R.error("文件为空!");
        }

        if (!LanguageEnum.isValidLanguage(lang)) {
            return R.error("不支持的语言!");
        }

        String filename = UUID.randomUUID() + file.getOriginalFilename();
        if (lang.equals("java")) filename = file.getOriginalFilename();
        Path path = Paths.get(resourceProperties.getCodePath() + filename);

        try {
            file.transferTo(path);
        } catch (Exception e) {
            return R.error("文件上传失败, "+path.toAbsolutePath()+"地址不存在!");
        }

        ExecuteMessage executeMessage = sandboxService.runCodeInSandbox(path.toUri().getPath(), filename, lang);

        return R.ok(executeMessage);
    }

    @PostMapping("/problem")
    @ApiOperation("测评")
    public R<ExecuteMessage> validate(@NotNull String pid, @RequestParam("file") MultipartFile file, @RequestParam("language") @NotBlank String lang) {
        if (file.isEmpty()) {
            return R.error("文件为空!");
        }

        if (!LanguageEnum.isValidLanguage(lang)) {
            return R.error("不支持的语言!");
        }

        String filename = UUID.randomUUID() + file.getOriginalFilename();
        if (lang.equals("java")) filename = file.getOriginalFilename();
        Path path = Paths.get(resourceProperties.getProblemCodePath() + filename);

        try {
            file.transferTo(path);
        } catch (Exception e) {
            return R.error("文件上传失败, "+path.toAbsolutePath()+"地址不存在!");
        }

        ExecuteMessage executeMessage = sandboxService.runProblemCodeInSandbox(path.toUri().getPath(), filename, lang, pid);

        return R.ok(executeMessage);
    }

    // @GetMapping("/{id}")
    // @ApiOperation("查询用户接口")
    // public R<String> getUser(@PathVariable("id") @NotNull String id) {
    //     for (LanguageEnum language : LanguageEnum.values) {
    //         if (language.getLanguage().equals(id)) {
    //             String res = runCodeWithoutInput.run(language, "main"+language.getSuffix());
    //             return R.ok(res);
    //         }
    //     }
    //     return R.error("未找到对应语言");
    // }

}
