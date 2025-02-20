package com.soli.frankit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName  com.soli.frankit.dto
 * fileName     TokenResponse
 * author       eumsoli
 * date         2025-02-20
 * description  로그인 시 반환할 JWT 토큰 응답 DTO
 */
@Getter
@AllArgsConstructor
public class TokenResponse {
    private final String token;
}

