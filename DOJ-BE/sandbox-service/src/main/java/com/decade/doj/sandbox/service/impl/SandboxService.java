package com.decade.doj.sandbox.service.impl;

import com.decade.doj.sandbox.config.DockerConfig.RunCodeWithoutInput;
import com.decade.doj.sandbox.config.DockerConfig.RunCodeWithInput;
import com.decade.doj.sandbox.config.properties.RemoteProperty;
import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
import com.decade.doj.sandbox.enums.LanguageEnum;
import com.decade.doj.sandbox.service.ISandboxService;
import com.decade.doj.sandbox.utils.SFTPUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SandboxService implements ISandboxService {

    private final RunCodeWithoutInput runCodeWithoutInput;

    private final RunCodeWithInput runCodeWithInput;

    private final RemoteProperty remoteProperty;

    @Override
    public ExecuteMessage runCodeInSandbox(String localPath, String filename, String lang) {
        SFTPUtil.uploadFile(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), localPath, remoteProperty.getScriptPath()+"/run");
        LanguageEnum languageEnum = LanguageEnum.getLanguageEnum(lang);
        if (languageEnum == null) {
            return new ExecuteMessage()
                    .setMessage("不支持的语言!");
        }
        return runCodeWithoutInput.run(languageEnum, filename);
    }

    @Override
    public ExecuteMessage runProblemCodeInSandbox(String localPath, String filename, String lang, String pid) {
        SFTPUtil.uploadFile(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), localPath, remoteProperty.getScriptPath()+"/problem/"+pid);
        LanguageEnum languageEnum = LanguageEnum.getLanguageEnum(lang);
        if (languageEnum == null) {
            return new ExecuteMessage()
                    .setMessage("不支持的语言!");
        }
        return runCodeWithInput.run(languageEnum, filename, pid);
    }

}
