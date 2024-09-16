package com.decade.doj.sandbox.utils;

import com.decade.doj.sandbox.enums.LanguageEnum;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

@Slf4j
public class SSHUtil {

    private static final int TIMEOUT_SECONDS = 30; // 设置超时秒数

    public static String executeRemoteCommand(String host, String user, String password, String command) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            try {
                JSch jsch = new JSch();
                Session session = jsch.getSession(user, host, 22);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);
                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);

                BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.connect();

                StringBuilder result = new StringBuilder();
                String msg;
                while ((msg = in.readLine()) != null) {
                    result.append(msg).append("\n");
                }

                channel.disconnect();
                session.disconnect();

                return result.toString();
            } catch (Exception e) {
                log.error("Error executing remote command", e);
                return "Error executing command: " + e.getMessage();
            }
        });

        try {
            return future.get(TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // 取消任务
            log.error("Command execution timed out");
            return "Command execution timed out";
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error executing command");
            return "Error executing command: " + e.getMessage();
        } finally {
            executor.shutdownNow();
        }
    }

    public static String executeRemoteCommand(String host, String user, String password, String command, Integer maxTimeLimit) {
        maxTimeLimit += 2;
        log.info("maxTimeLimit: " + maxTimeLimit);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        long stime = 0, etime = 0;
        try {
            stime = System.currentTimeMillis();
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            StringBuilder result = new StringBuilder();
            channel.connect();
            etime = System.currentTimeMillis();
            log.info("ssh connect time: " + (etime - stime) + "ms");
            Future future = executor.submit(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        result.append(msg).append("\n");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                stime = System.currentTimeMillis();
                future.get(maxTimeLimit, java.util.concurrent.TimeUnit.SECONDS);
                etime = System.currentTimeMillis();
                log.info("ssh execute time: " + (etime - stime) + "ms");
                channel.disconnect();
                session.disconnect();
                return result.toString();
            } catch (TimeoutException e) {
                future.cancel(true); // 取消任务
                log.error("Command execution timed out");
                return "3";
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error executing command", e);
                return "9999";
            } finally {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Error executing remote command", e);
            return "Error executing command: " + e.getMessage();
        }
    }
}