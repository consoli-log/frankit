package com.soli.frankit.service;

import com.soli.frankit.config.TestEnvConfig;
import com.soli.frankit.dto.UserRequest;
import com.soli.frankit.dto.UserResponse;
import com.soli.frankit.entity.User;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * packageName  com.soli.frankit.service
 * fileName     UserServiceTest
 * author       eumsoli
 * date         2025-02-19
 * description  UserService의 회원가입 서비스 테스트
 */

@ExtendWith(MockitoExtension.class)
@Import(TestEnvConfig.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserRequest validRequest;
    private UserRequest duplicateRequest;

    @BeforeEach
    void setUp() {
        validRequest = new UserRequest("soli@test.com", "solitest1216");
        duplicateRequest = new UserRequest("duplicate@test.com", "solitest1216");
    }

    @Test
    @DisplayName("회원가입 성공")
    void registerSuccess() {
        // Given
        when(userRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            return new User(savedUser.getEmail(), savedUser.getPassword());
        });

        // When
        UserResponse response = userService.register(validRequest);
        response = new UserResponse(response.getEmail(), LocalDateTime.now());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(validRequest.getEmail());
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void registerFail_DuplicateEmail() {
        // Given
        when(userRepository.existsByEmail(duplicateRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.register(duplicateRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
    }

}