package com.decade.doj.sandbox.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class SFTPUtil {

    public static void uploadFile(String host, String user, String password, String localFilePath, String remoteDir) {
        log.info("localFilePath: {}, remoteDir: {}", localFilePath, remoteDir);
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            sftpChannel.put(new FileInputStream(localFilePath), remoteDir + "/" + localFilePath.substring(localFilePath.lastIndexOf("/") + 1));

            sftpChannel.exit();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}