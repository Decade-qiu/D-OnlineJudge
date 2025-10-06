package com.decade.doj.sandbox.service;

import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.domain.vo.JudgingTask;

import java.util.concurrent.CompletableFuture;

public interface ISandboxService {

    CompletableFuture<ExecuteMessage> runCodeInSandbox(String localPath, String filename, String lang);

    CompletableFuture<ExecuteMessage> runCodeInSandboxWI(String localPath, String inputname, String filename, String lang);

    void execute(JudgingTask task);
}
