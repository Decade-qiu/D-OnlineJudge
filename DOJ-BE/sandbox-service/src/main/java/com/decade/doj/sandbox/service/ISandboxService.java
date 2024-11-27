package com.decade.doj.sandbox.service;

import com.decade.doj.sandbox.domain.vo.ExecuteMessage;

public interface ISandboxService {

    ExecuteMessage runCodeInSandbox(String localPath, String filename, String lang);

    ExecuteMessage runProblemCodeInSandbox(String localPath, String filename, String lang, String pid);
}
