package com.decade.doj.common.config.custom;

import com.decade.doj.common.config.properties.ResourceProperties;
import com.decade.doj.common.interceptor.AdminCheckInterceptor;
import com.decade.doj.common.interceptor.IdentityInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass(DispatcherServlet.class)
@EnableConfigurationProperties(ResourceProperties.class)
@Slf4j
@RequiredArgsConstructor
public class MVCConfig implements WebMvcConfigurer {

    private final ResourceProperties resourceProperties;
    private final IdentityInterceptor identityInterceptor;
    private final AdminCheckInterceptor adminCheckInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String request = resourceProperties.getRequest() + "**";
        String location = "file:" + resourceProperties.getLocation();
        log.info("request: {}, location: {}", request, location);
        registry.addResourceHandler(request).addResourceLocations(location);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 身份识别拦截器，必须先执行
        registry.addInterceptor(identityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**", "/doc.html/**", "/actuator/**");
        
        // 管理员权限拦截器，后执行
        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/**");
    }
}