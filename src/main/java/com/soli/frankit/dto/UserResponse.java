package com.soli.frankit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName  com.soli.frankit.dto
 * fileName     UserResponse
 * author       eumsoli
 * date         2025-02-24
 * description  회원가입 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 응답 DTO")
public class UserResponse {

    @Schema(description = "사용자 이메일", example = "soli@email.com")
    private String email;

    @Schema(description = "회원가입 날짜", example = "2025-02-24T12:34:56")
    private LocalDateTime createdAt;
}
