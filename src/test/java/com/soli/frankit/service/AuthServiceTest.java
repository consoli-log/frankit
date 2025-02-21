package com.soli.frankit.service;

import com.soli.frankit.config.TestEnvConfig;
import com.soli.frankit.dto.LoginRequest;
import com.soli.frankit.dto.TokenResponse;
import com.soli.frankit.entity.User;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.UserRepository;
import com.soli.frankit.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * packageName  com.soli.frankit.service
 * fileName     AuthServiceTest
 * author       eumsoli
 * date         2025-02-20
 * description  AuthService의 로그인 기능 테스트
 */

@ExtendWith(MockitoExtension.class)
@Import(TestEnvConfig.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private LoginRequest validRequest;
    private LoginRequest wrongPasswordRequest;
    private LoginRequest emailNotFoundRequest;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest("soli@test.com", "solitest1216");
        wrongPasswordRequest = new LoginRequest("soli@test.com", "wrongpassword");
        emailNotFoundRequest = new LoginRequest("notfound@test.com", "solitest1216");
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // Given
        User user = new User(validRequest.getEmail(), passwordEncoder.encode(validRequest.getPassword()));

        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(validRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(any())).thenReturn("JWT_Token");

        // When
        TokenResponse response = authService.login(validRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("JWT_Token");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void loginFail_WrongPassword() {
        // Given
        User user = new User(validRequest.getEmail(), passwordEncoder.encode("correctpassword"));

        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(wrongPasswordRequest.getPassword(), user.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(wrongPasswordRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - 이메일이 존재하지 않음")
    void loginFail_EmailNotFound() {
        // Given
        when(userRepository.findByEmail(emailNotFoundRequest.getEmail())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(emailNotFoundRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

}