package com.decade.doj.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "doj.auth")
public class AuthProperties {
    private List<String> includePaths;
    private List<String> excludePaths;
}
