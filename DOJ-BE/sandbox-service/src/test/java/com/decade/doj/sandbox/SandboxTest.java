package com.decade.doj.sandbox;

import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.service.ISandboxService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SandboxTest {

    @Autowired
    private ISandboxService sandboxService;

    @Test
    public void testRunCode() {
        String filePath = "/Users/qzj/Desktop/Development/D-OnlineJudge/static/docker-sandbox/python/Main.py"; // 替换为实际的文件路径
        String filename = "Main.py"; // 替换为实际的文件名
        String lang = "python";

        // try {
        //     ExecuteMessage res = sandboxService.runCodeInSandbox(filePath, filename, lang);
        //     System.out.println(res);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }

}
