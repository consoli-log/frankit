package com.soli.frankit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * packageName  : com.soli.frankit.config
 * fileName     : SecurityConfig
 * author       : eumsoli
 * date         : 2025-02-18
 * description  : Spring Security 설정
 */

@Configuration
public class SecurityConfig {

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}



