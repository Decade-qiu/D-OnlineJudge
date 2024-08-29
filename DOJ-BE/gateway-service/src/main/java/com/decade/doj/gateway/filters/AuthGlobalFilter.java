package com.decade.doj.gateway.filters;

import cn.hutool.core.text.AntPathMatcher;
import com.decade.doj.common.config.properties.JwtProperties;
import com.decade.doj.common.domain.R;
import com.decade.doj.common.exception.UnauthorizedException;
import com.decade.doj.common.config.custom.JwtTool;
import com.decade.doj.gateway.config.properties.AuthProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTool jwtTool;
    private final AuthProperties authProperties;
    private final JwtProperties jwtProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isExcludedPath(path)) {
            ServerWebExchange build = exchange.mutate()
                    .request(builder -> builder.header(jwtProperties.getSecretKey(), String.valueOf(0)))
                    .build();
            return chain.filter(build);
        }
        log.info("path:{}", path);
        log.info("headers:{}", exchange.getRequest().getHeaders().entrySet());
        String token = exchange.getRequest().getHeaders().getFirst(jwtProperties.getAuthorization());
        Long userId;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            return handleUnauthorizedResponse(exchange);
        }
        // 校验成功，将userId放入请求头
        ServerWebExchange build = exchange.mutate()
                .request(builder -> builder.header(jwtProperties.getSecretKey(), String.valueOf(userId)))
                .build();
        return chain.filter(build);
    }

    public Mono<Void> handleUnauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        R<String> response = R.error(401, "登陆过期，请重新登陆！");

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            DataBuffer dataBuffer = bufferFactory.wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        } catch (Exception e) {
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isExcludedPath(String path) {
        for (String ignorePath : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(ignorePath, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
