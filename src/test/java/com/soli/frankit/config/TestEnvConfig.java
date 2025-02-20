package com.soli.frankit.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * packageName  com.soli.frankit.config
 * fileName     TestEnvConfig
 * author       eumsoli
 * date         2025-02-20
 * description  테스트 환경에서 환경 변수 로드
 */

@Configuration
public class TestEnvConfig {

    static {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

}
