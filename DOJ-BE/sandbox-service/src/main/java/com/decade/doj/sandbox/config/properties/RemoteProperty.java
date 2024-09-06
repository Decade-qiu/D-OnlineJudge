package com.decade.doj.sandbox.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "doj.remote")
public class RemoteProperty {

    private String host;
    private String user;
    private String password;
    private String scriptPath;

}
