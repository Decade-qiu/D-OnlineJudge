package com.decade.doj.user;

import com.decade.doj.common.config.custom.JwtTool;
import com.decade.doj.common.config.custom.MVCConfig;
import com.decade.doj.common.config.custom.MybatisConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@MapperScan("com.decade.doj.user.mapper")
@Import({JwtTool.class, MVCConfig.class, MybatisConfig.class})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
