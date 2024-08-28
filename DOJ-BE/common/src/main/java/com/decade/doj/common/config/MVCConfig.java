package com.decade.doj.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass(DispatcherServlet.class)
@EnableConfigurationProperties(ResourceProperties.class)
@Slf4j
@RequiredArgsConstructor
public class MVCConfig implements WebMvcConfigurer {

    private final ResourceProperties resourceProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String request = resourceProperties.getRequest() + "**";
        String location = "file:" + resourceProperties.getLocation();
        log.info("request: {}, location: {}", request, location);
        registry.addResourceHandler(request).addResourceLocations(location);
    }
}