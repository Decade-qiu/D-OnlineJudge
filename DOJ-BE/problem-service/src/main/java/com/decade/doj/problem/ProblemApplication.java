package com.decade.doj.problem;

import com.decade.doj.common.config.custom.JwtTool;
import com.decade.doj.common.config.custom.MVCConfig;
import com.decade.doj.common.config.custom.MybatisConfig;
import com.decade.doj.common.interceptor.IdentityInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.decade.doj.problem.mapper")
@EnableCaching
@Import({MVCConfig.class, MybatisConfig.class, IdentityInterceptor.class})
public class ProblemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProblemApplication.class, args);
    }
}
