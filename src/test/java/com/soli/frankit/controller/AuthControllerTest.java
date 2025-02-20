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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @MockitoBean
    private AuthService authService;

    private LoginRequest validRequest;
    private LoginRequest wrongFormatEmailRequest;
    private LoginRequest wrongPasswordRequest;
    private LoginRequest blankEmailRequest;
    private LoginRequest blankPasswordRequest;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest("soli@test.com", "solitest1216");
        wrongFormatEmailRequest = new LoginRequest("wrongformat-email", "solitest1216");
        wrongPasswordRequest = new LoginRequest("soli@test.com", "wrongpassword");
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
                .andExpect(jsonPath("$.error").value("이메일 또는 비밀번호가 올바르지 않습니다."));
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
    @DisplayName("로그인 실패 - 이메일 공백")
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