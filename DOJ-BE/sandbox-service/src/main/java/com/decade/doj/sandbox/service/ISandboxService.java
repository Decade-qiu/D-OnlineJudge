package com.decade.doj.sandbox.service;

import com.decade.doj.sandbox.domain.vo.ExecuteMessage;

import java.util.concurrent.CompletableFuture;

public interface ISandboxService {

    CompletableFuture<ExecuteMessage> runCodeInSandbox(String localPath, String filename, String lang);

    ExecuteMessage runProblemCodeInSandbox(String localPath, String filename, String lang, String pid);
}
