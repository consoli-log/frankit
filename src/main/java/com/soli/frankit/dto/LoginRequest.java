package com.soli.frankit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * packageName  com.soli.frankit.dto
 * fileName     LoginRequest
 * author       eumsoli
 * date         2025-02-19
 * description  로그인 요청을 처리하는 DTO
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {

    @Schema(description = "사용자 이메일", example = "soli@email.com")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "사용자 비밀번호 (최소 8자)", example = "solipassword1216")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

}

