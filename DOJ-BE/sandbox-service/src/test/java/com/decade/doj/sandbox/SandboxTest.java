package com.decade.doj.sandbox;

import com.decade.doj.sandbox.config.DockerConfig;
import com.decade.doj.sandbox.config.DockerConfig.RunCodeWithoutInput;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.utils.SFTPUtil;
import com.decade.doj.sandbox.utils.SSHUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SandboxTest {

    @Autowired
    private RunCodeWithoutInput runCodeWithoutInput;



}
