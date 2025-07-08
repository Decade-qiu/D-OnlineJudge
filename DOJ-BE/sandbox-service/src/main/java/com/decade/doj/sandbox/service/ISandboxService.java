package com.decade.doj.sandbox.service;

import com.decade.doj.sandbox.domain.vo.ExecuteMessage;

import java.util.concurrent.CompletableFuture;

public interface ISandboxService {

    CompletableFuture<ExecuteMessage> runCodeInSandbox(String localPath, String filename, String lang);

    CompletableFuture<ExecuteMessage> runCodeInSandboxWI(String localPath, String inputname, String filename, String lang);

    CompletableFuture<ExecuteMessage> runCodeInSandboxWIV(String localPath, String inputname, String output, String filename, String lang, Long pid, String code, Long uid);
}
