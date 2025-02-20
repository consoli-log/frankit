package com.soli.frankit.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName  com.soli.frankit.config
 * fileName     JwtSecretLoadTest
 * author       eumsoli
 * date         2025-02-20
 * description  JWT_SECRET 환경 변수 로드 테스트
 */

@SpringBootTest
@Import(TestEnvConfig.class)
public class JwtSecretLoadTest {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    void jwtSecret_shouldLoadFromEnvironment() {
        assertThat(jwtSecret).as("JWT_SECRET 값이 로드되지 않았습니다.").isNotNull();
        System.out.println("Loaded JWT Secret from @Value: " + jwtSecret);
        System.out.println("Loaded JWT Secret from System.getenv: " + System.getProperty("JWT_SECRET"));
    }
}

