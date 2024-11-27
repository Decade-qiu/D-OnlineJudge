package com.decade.doj.common.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "doj.resource")
public class ResourceProperties {

    private String request;
    private String location;
    private String codePath;
    private String problemCodePath;

}
