package com.decade.doj.common.config.custom;

import com.decade.doj.common.config.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.PrivateKey;
import java.security.PublicKey;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    @Bean
    public KeyPair keyPair(JwtProperties properties) throws Exception {
        // 加载密钥库
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream inputStream = properties.getLocation().getInputStream()) {
            keyStore.load(inputStream, properties.getPassword().toCharArray());
        }

        // 读取私钥和证书
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(properties.getAlias(), properties.getPassword().toCharArray());
        Certificate certificate = keyStore.getCertificate(properties.getAlias());
        PublicKey publicKey = certificate.getPublicKey();

        return new KeyPair(publicKey, privateKey);
    }
}