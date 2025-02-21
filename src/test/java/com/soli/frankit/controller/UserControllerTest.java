package com.soli.frankit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soli.frankit.config.TestSecurityConfig;
import com.soli.frankit.dto.RegisterRequest;
import com.soli.frankit.entity.User;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.service.UserService;
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
 * fileName     UserControllerTest
 * author       eumsoli
 * date         2025-02-19
 * description  UserController의 회원가입 API 테스트
 */

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private RegisterRequest validRequest;
    private RegisterRequest invalidRequest;
    private RegisterRequest duplicateRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest("soli@test.com", "solitest1216");
        invalidRequest = new RegisterRequest("", null);
        duplicateRequest = new RegisterRequest("duplicate@test.com", "solitest1216");
    }

    @Test
    @DisplayName("회원가입 성공 (200)")
    void registerSuccess() throws Exception {
        // Given
        User user = new User(validRequest.getEmail(), "encodedPassword");
        when(userService.register(any(RegisterRequest.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(validRequest.getEmail()))
                .andExpect(jsonPath("$.password").value("encodedPassword"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복 (409)")
    void registerFail_DuplicateEmail() throws Exception {
        // Given
        doThrow(new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS))
                .when(userService).register(any(RegisterRequest.class));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage()));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검사 실패 (400)")
    void registerFail_ValidationError() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("이메일은 필수 입력값입니다."))
                .andExpect(jsonPath("$.password").value("비밀번호는 필수 입력값입니다."));
    }

}
