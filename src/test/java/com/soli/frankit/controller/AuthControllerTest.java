package com.soli.frankit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soli.frankit.config.TestSecurityConfig;
import com.soli.frankit.dto.LoginRequest;
import com.soli.frankit.dto.TokenResponse;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName  com.soli.frankit.controller
 * fileName     AuthControllerTest
 * author       eumsoli
 * date         2025-02-20
 * description  AuthController의 로그인 API 테스트
 */

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private LoginRequest validRequest;
    private LoginRequest emailNotFoundRequest;
    private LoginRequest wrongPasswordRequest;
    private LoginRequest wrongFormatEmailRequest;
    private LoginRequest blankEmailRequest;
    private LoginRequest blankPasswordRequest;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest("soli@test.com", "solitest1216");
        emailNotFoundRequest = new LoginRequest("notfound@test.com", "solitest1216");
        wrongPasswordRequest = new LoginRequest("soli@test.com", "wrongpassword");
        wrongFormatEmailRequest = new LoginRequest("wrongformat-email", "solitest1216");
        blankEmailRequest = new LoginRequest("", "solitest1216");
        blankPasswordRequest = new LoginRequest("soli@test.com", "");
    }

    @Test
    @DisplayName("로그인 성공 (200)")
    void loginSuccess() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(new TokenResponse("JWT_Token"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("JWT_Token"));
    }

    @Test
    @DisplayName("로그인 실패 - 이메일이 존재하지 않음 (404)")
    void loginFail_EmailNotFound() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND))
                .when(authService).login(any(LoginRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(emailNotFoundRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(ErrorCode.MEMBER_NOT_FOUND.getMessage()));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림 (401)")
    void loginFail_WrongPassword() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.INVALID_CREDENTIALS))
                .when(authService).login(any(LoginRequest.class));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value(ErrorCode.INVALID_CREDENTIALS.getMessage()));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 이메일 형식 (400)")
    void loginFail_WrongFormatEmail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(wrongFormatEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("올바른 이메일 형식이 아닙니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 공백 (400)")
    void loginFail_BlankEmail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(blankEmailRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("이메일은 필수 입력값입니다."));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 공백 (400)")
    void loginFail_BlankPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(blankPasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("비밀번호는 필수 입력값입니다."));
    }

}