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
 * description  회원가입 완료 후 응답 DTO (비밀번호 제외)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @Schema(description = "사용자 이메일", example = "soli@email.com")
    private String email;

    @Schema(description = "회원가입 날짜", example = "2025-02-24T12:34:56")
    private LocalDateTime createdAt;
}
