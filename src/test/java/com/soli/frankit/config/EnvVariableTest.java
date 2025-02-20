package com.soli.frankit.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName  com.soli.frankit.config
 * fileName     EnvVariableTest
 * author       eumsoli
 * date         2025-02-20
 * description  DB_PORT 환경 변수 로드 테스트
 */

@SpringBootTest
@Import(TestEnvConfig.class)
public class EnvVariableTest {

    @Value("${DB_PORT}")
    private String dbPort;

    @Test
    void dbPort_shouldLoadFromEnvironment() {
        assertThat(dbPort).isNotNull();
        System.out.println("Loaded DB_PORT: " + dbPort);
    }
}
