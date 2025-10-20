package com.decade.doj.submission;

import com.decade.doj.common.config.custom.DefaultFeignConfig;
import com.decade.doj.common.config.custom.JwtTool;
import com.decade.doj.common.config.custom.MVCConfig;
import com.decade.doj.common.config.custom.MybatisConfig;
import com.decade.doj.common.interceptor.AdminCheckInterceptor;
import com.decade.doj.common.interceptor.IdentityInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.decade.doj.submission.mapper")
@EnableFeignClients(basePackages = "com.decade.doj.common.client", defaultConfiguration = DefaultFeignConfig.class)
@Import({JwtTool.class, MVCConfig.class, MybatisConfig.class, IdentityInterceptor.class, AdminCheckInterceptor.class})
public class SubmissionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubmissionApplication.class, args);
    }
}
