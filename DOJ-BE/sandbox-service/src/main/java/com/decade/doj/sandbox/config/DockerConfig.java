package com.decade.doj.sandbox.config;

import com.decade.doj.common.utils.LocalResource;
import com.decade.doj.sandbox.config.properties.RemoteProperty;
import com.decade.doj.sandbox.domain.vo.ExecuteMessage;
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
import java.util.List;

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
        r = mkdir(remoteProperty.getScriptPath() + "/run");
        log.info("mkdir /run: {}", r);
        // create run.py
        upload("docker/run.py", "/run");
        // install docker container
        for (LanguageEnum language : LanguageEnum.values) {
            createDockerContainer(language);
        }
    }

    public void createDockerContainer(LanguageEnum language) {
        String cmd = "docker ps -a --format {{.Names}} | grep " + language.getDockerName();
        log.info("check docker container cmd: {}", cmd);
        String res = SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);
        if (res != null && res.strip().equals(language.getDockerName())) {
            log.info("docker container {} already exists", language.getDockerName());
            // restart container
            cmd = "docker start " + language.getDockerName();
            SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);
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
        SFTPUtil.uploadFile(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), local, remoteProperty.getScriptPath() + remote);
    }

    public String mkdir(String path) {
        String cmd = "mkdir -p " + path;
        log.info("mkdir cmd: {}", cmd);
        return SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);
    }

    public interface RunCodeWithoutInput {
        ExecuteMessage run(LanguageEnum language, String source);
    }

    @Bean
    public RunCodeWithoutInput runCode(RemoteProperty remoteProperty) {
        return (language, source) -> {
            // script path
            String script = remoteProperty.getScriptPath() + WITHOUT_INPUT_FILE;
            String cmd = "python3 " + script + " ";
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
            log.info(cmd);
            // execute command
            String origin = SSHUtil.executeRemoteCommand(remoteProperty.getHost(), remoteProperty.getUser(), remoteProperty.getPassword(), cmd);
            if (origin != null) {
                List<String> res = List.of(origin.split("\n"));
                List<String> status = List.of(res.get(0).split(" "));
                return new ExecuteMessage()
                        .setExitValue(Integer.parseInt(status.get(0).strip()))
                        .setTime(Double.parseDouble(status.get(1).strip()))
                        .setMemory(Long.valueOf(status.get(2).strip()))
                        .setMessage(
                                res.subList(1, res.size()).stream().reduce((a, b) -> a + "\n" + b).orElse("")
                        );
            }
            return new ExecuteMessage()
                    .setMessage("执行失败!");
        };
    }
}
