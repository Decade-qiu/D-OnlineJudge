package com.decade.doj.sandbox;

import com.decade.doj.sandbox.utils.SFTPUtil;
import com.decade.doj.sandbox.utils.SSHUtil;
import org.junit.jupiter.api.Test;

public class SandboxTest {

    @Test
    public void testSFTP() {
        SFTPUtil.uploadFile("192.168.74.131", "root", "1", "D:/桌面/DOJ/DOJ-BE/sandbox-service/src/main/resources/test/main.py", "/root/doj/sandbox/code/run");

        String cmd = "python3 /root/doj/sandbox/code/run/run.py python";
        String res = SSHUtil.executeRemoteCommand("192.168.74.131", "root", "1", cmd);
        System.out.println(res);
    }

    @Test
    public void testSSH() {
        String cmd = "/root/doj/sandbox/demo.sh";
        String res = SSHUtil.executeRemoteCommand("192.168.74.131", "root", "1", cmd);
        System.out.println(res);
    }

}
