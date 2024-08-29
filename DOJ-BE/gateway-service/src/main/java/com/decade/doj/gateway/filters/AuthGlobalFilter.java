package com.decade.doj.gateway.filters;

import cn.hutool.core.text.AntPathMatcher;
import com.decade.doj.common.config.properties.JwtProperties;
import com.decade.doj.common.exception.UnauthorizedException;
import com.decade.doj.common.config.custom.JwtTool;
import com.decade.doj.gateway.config.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // 校验成功，将userId放入请求头
        ServerWebExchange build = exchange.mutate()
                .request(builder -> builder.header(jwtProperties.getSecretKey(), String.valueOf(userId)))
                .build();
        return chain.filter(build);
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
