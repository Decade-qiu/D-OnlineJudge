package com.decade.doj.sandbox.controller;

import cn.hutool.core.bean.BeanUtil;
import com.decade.doj.common.domain.R;
import com.decade.doj.sandbox.config.DockerConfig;
import com.decade.doj.sandbox.config.DockerConfig.RunCodeWithoutInput;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.utils.SFTPUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/sandbox")
@Api(tags = "沙箱相关接口")
@Slf4j
@RequiredArgsConstructor
public class SandboxController {

    private final RunCodeWithoutInput runCodeWithoutInput;

    @GetMapping("/{id}")
    @ApiOperation("查询用户接口")
    public R<String> getUser(@PathVariable("id") @NotNull String id) {
        for (LanguageEnum language : LanguageEnum.values) {
            if (language.getLanguage().equals(id)) {
                String res = runCodeWithoutInput.run(language, "main"+language.getSuffix());
                return R.ok(res);
            }
        }
        return R.error("未找到对应语言");
    }

}
