package com.decade.doj.sandbox;

import com.decade.doj.common.config.custom.DefaultFeignConfig;
import com.decade.doj.common.config.custom.MVCConfig;
import com.decade.doj.common.config.custom.MybatisConfig;
import com.decade.doj.common.config.thread.ThreadPoolConfig;
import com.decade.doj.common.interceptor.AdminCheckInterceptor;
import com.decade.doj.common.interceptor.IdentityInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.decade.doj.common.client", defaultConfiguration = DefaultFeignConfig.class)
@Import({ThreadPoolConfig.class, MVCConfig.class, MybatisConfig.class, IdentityInterceptor.class, AdminCheckInterceptor.class})
public class SandboxApplication {
    public static void main(String[] args) {
        SpringApplication.run(SandboxApplication.class, args);
    }
}
