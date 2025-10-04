package com.decade.doj.common.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppNameProperties {

    @Value("${spring.application.name}")
    private String name;
}
