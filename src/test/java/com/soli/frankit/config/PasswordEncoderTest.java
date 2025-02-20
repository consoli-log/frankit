package com.soli.frankit.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * packageName  com.soli.frankit.config
 * fileName     PasswordEncoderTest
 * author       eumsoli
 * date         2025-02-19
 * description  BCryptPasswordEncoder 암호화 테스트
 */

@SpringBootTest
@Import(TestEnvConfig.class)
public class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("비밀번호 암호화")
    void encodedPassword() {
        String password = "solitest1216"; // 원래 비밀번호
        String encodedPassword = passwordEncoder.encode(password); // BCrypt로 암호화

        System.out.println("Encoded Password: " + encodedPassword);

        // 같은 비밀번호라도 매번 다른 값이 생성됨
        assertNotEquals(password, encodedPassword);
        assertTrue(passwordEncoder.matches(password, encodedPassword)); // 매칭 테스트
    }
}

