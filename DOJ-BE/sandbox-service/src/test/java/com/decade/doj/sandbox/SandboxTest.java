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

    @Test
    public void testBean() {
        String res = runCodeWithoutInput.run(LanguageEnum.PYTHON, "20240906.py");
        System.out.println(res);
    }

    @Test
    public void testPython() {
        SFTPUtil.uploadFile("192.168.74.131", "root", "1", "D:/桌面/DOJ/DOJ-BE/sandbox-service/src/main/resources/test/20240906.py", "/root/doj/sandbox/code/run");

        // String cmd = "python3 /root/doj/sandbox/code/run/run.py python";
        // String res = SSHUtil.executeRemoteCommand("192.168.74.131", "root", "1", cmd);
        // System.out.println(res);
    }

    @Test
    public void testJava() {
        SFTPUtil.uploadFile("192.168.74.131", "root", "1", "D:/桌面/DOJ/DOJ-BE/sandbox-service/src/main/resources/test/Main.java", "/root/doj/sandbox/code/run");

        String cmd = "python3 /root/doj/sandbox/code/run/run.py java";
        String res = SSHUtil.executeRemoteCommand("192.168.74.131", "root", "1", cmd);
        System.out.println(res);
    }

    @Test
    public void testCPP() {
        SFTPUtil.uploadFile("192.168.74.131", "root", "1", "D:/桌面/DOJ/DOJ-BE/sandbox-service/src/main/resources/test/main.cpp", "/root/doj/sandbox/code/run");

        String cmd = "python3 /root/doj/sandbox/code/run/run.py cpp";
        String res = SSHUtil.executeRemoteCommand("192.168.74.131", "root", "1", cmd);
        System.out.println(res);
    }

}
