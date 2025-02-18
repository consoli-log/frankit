package com.soli.frankit.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * packageName  com.soli.frankit
 * fileName     JwtSecretLoadTest
 * author       eumsoli
 * date         2025-02-18
 * description  JWT_SECRET 환경 변수 로드 테스트
 */

@SpringBootTest
public class JwtSecretLoadTest {

    @BeforeAll
    static void loadEnvVariables() {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    void jwtSecret_shouldLoadFromEnvironment() {
        Assertions.assertNotNull(jwtSecret, "JWT_SECRET 값이 로드되지 않았습니다.");
        System.out.println("Loaded JWT Secret from @Value: " + jwtSecret);
        System.out.println("Loaded JWT Secret from System.getenv: " + System.getProperty("JWT_SECRET"));
    }
}

