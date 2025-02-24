package com.soli.frankit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "발급된 JWT 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzb2xpQHRlc3QuY29tIiwiaWF0IjoxNzQwMjI3MTU1LCJleHAiOjE3NDAzMTM1NTV9.he0ETX2SF6s7BRJD_aV3tNegvIopXEa6XXoeJpPVRc4")
    private final String token;

}

