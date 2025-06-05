package com.decade.doj.submission;

import com.decade.doj.common.config.custom.MVCConfig;
import com.decade.doj.common.config.custom.MybatisConfig;
import com.decade.doj.common.interceptor.IdentityInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.decade.doj.submission.mapper")
@Import({MVCConfig.class, MybatisConfig.class, IdentityInterceptor.class})
public class SubmissionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubmissionApplication.class, args);
    }
}
