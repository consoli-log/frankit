package com.soli.frankit.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class EnvVariableTest {

    @BeforeAll
    static void loadEnvVariables() {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    @Value("${DB_PORT}")
    private String dbPort;

    @Test
    void dbPort_shouldLoadFromEnvironment() {
        assertThat(dbPort).isNotNull();
        System.out.println("Loaded DB_PORT: " + dbPort);
    }
}
