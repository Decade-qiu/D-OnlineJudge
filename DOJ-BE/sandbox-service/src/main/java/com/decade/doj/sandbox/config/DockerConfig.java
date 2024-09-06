package com.decade.doj.sandbox.config;

import com.decade.doj.common.utils.LocalResource;
import com.decade.doj.sandbox.config.properties.RemoteProperty;
import com.decade.doj.sandbox.utils.SFTPUtil;
import com.decade.doj.sandbox.utils.SSHUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.decade.doj.sandbox.enums.LanguageEnum;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties(RemoteProperty.class)
@Slf4j
@RequiredArgsConstructor
public class DockerConfig {

    private final RemoteProperty remoteProperty;

    private final String WITHOUT_INPUT_FILE = "/run/run.py";

    @PostConstruct
    public void init() {
        String r = "";
        // create file directory
        r = mkdir(remoteProperty.getScriptPath());
        log.info("mkdir script path: {}", r);
        r = mkdir(Paths.get(remoteProperty.getScriptPath(), "/run").toString().replace("\\", "/"));
        log.info("mkdir /run: {}", r);
        // create run.py
        upload("docker/run.py", "/run");
        // install docker container
        for (LanguageEnum language : LanguageEnum.values) {
            createDockerContainer(language);
        }
    }

    public void createDockerContainer(LanguageEnum language) {
        String cmd = "docker ps --format {{.Names}} | grep " + language.getDockerName();
        log.info("check docker container cmd: {}", cmd);
        String res = SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);
        if (res != null && res.strip().equals(language.getDockerName())) {
            log.info("docker container {} already exists", language.getDockerName());
            return;
        }

        cmd = "docker run -d --name "
                + language.getDockerName()
                + " -v "
                + remoteProperty.getScriptPath()
                + ":/usr/src/app "
                + language.getDockerImage()
                + " tail -f /dev/null";
        log.info("create docker container cmd: {}", cmd);
        SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);

        cmd = "docker exec "
                + language.getDockerName()
                + " apt-get update";
        log.info("update apt-get cmd: {}", cmd);
        SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);

        cmd = "docker exec "
                + language.getDockerName()
                + " apt-get install time";
        log.info("install time cmd: {}", cmd);
        SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);
    }

    public void upload(String local, String remote) {
        local = LocalResource.getLocalFilePath(local);
        SFTPUtil.uploadFile(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), local, Paths.get(remoteProperty.getScriptPath(), remote).toString().replace("\\", "/"));
    }

    public String mkdir(String path) {
        String cmd = "mkdir -p " + path;
        log.info("mkdir cmd: {}", cmd);
        return SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);
    }

    public interface RunCodeWithoutInput {
        String run(LanguageEnum language, String source);
    }

    @Bean
    public RunCodeWithoutInput runCode(RemoteProperty remoteProperty) {
        return (language, source) -> {
            // script path
            Path script = Paths.get(remoteProperty.getScriptPath(), WITHOUT_INPUT_FILE);
            String cmd = "python3 " + script.toString().replace("\\", "/") + " ";
            // specify language
            if (language.equals(LanguageEnum.JAVA)) {
                cmd += language.getLanguage();
            } else if (language.equals(LanguageEnum.PYTHON)) {
                cmd += language.getLanguage();
            } else if (language.equals(LanguageEnum.CPP)) {
                cmd += language.getLanguage();
            }
            // specify source file
            cmd += " " + source;
            // execute command
            return SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);
        };
    }

}
