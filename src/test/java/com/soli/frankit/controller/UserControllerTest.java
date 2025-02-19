package com.soli.frankit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soli.frankit.config.SecurityConfigTest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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
@Import(SecurityConfigTest.class)
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
        validRequest = new RegisterRequest();
        validRequest.setEmail("soli@test.com");
        validRequest.setPassword("solitest1216");

        invalidRequest = new RegisterRequest();
        invalidRequest.setEmail(""); // 잘못된 이메일
        invalidRequest.setPassword(""); // 잘못된 비밀번호

        duplicateRequest = new RegisterRequest();
        duplicateRequest.setEmail("duplicate@test.com"); // 중복 이메일
        duplicateRequest.setPassword("solitest1216");
    }

    @Test
    @DisplayName("회원가입 성공 (200)")
    void registerSuccess() throws Exception {
        // Given
        User mockUser = new User(validRequest.getEmail(), "encodedPassword");
        when(userService.register(any(RegisterRequest.class))).thenReturn(mockUser);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(validRequest.getEmail()))
                .andExpect(jsonPath("$.password").value("encodedPassword"));
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

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복 (409)")
    void registerFail_DuplicateEmail() throws Exception {
        // Given: 이미 존재하는 이메일이면 예외 발생하도록 설정
        doThrow(new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS))
                .when(userService).register(any(RegisterRequest.class));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("이미 존재하는 이메일입니다."));
    }

}
