package com.decade.doj.gateway.filters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CrossFilter {
    /**
     * 添加跨域过滤器
     * @return
     */
    @Bean
    public CorsWebFilter corsWebFilter(){
        //基于url跨域，选择reactive包下的
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 跨域配置信息
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许跨域的头
        configuration.addAllowedHeader("*");
        // 允许跨域的请求方式
        configuration.addAllowedMethod("*");
        // 允许跨域的请求来源
        configuration.addAllowedOrigin("http://localhost:5174");
        configuration.addAllowedOrigin("http://localhost:5173");
        // 预检请求的有效期，单位为秒
        configuration.setMaxAge(3600L);
        // 是否允许携带cookie跨域
        configuration.setAllowCredentials(true);
        // 任意url都要进行跨域配置
        source.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(source);
    }
}