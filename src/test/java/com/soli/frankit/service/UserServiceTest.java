package com.soli.frankit.service;

import com.soli.frankit.dto.RegisterRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private RegisterRequest validRequest;
    private RegisterRequest duplicateRequest;


    @BeforeEach
    void setUp() {
        validRequest = new RegisterRequest();
        validRequest.setEmail("soli@test.com");
        validRequest.setPassword("solitest1216");

        duplicateRequest = new RegisterRequest();
        duplicateRequest.setEmail("duplicate@test.com"); // 중복 이메일
        duplicateRequest.setPassword("solitest1216");
    }

    @Test
    @DisplayName("회원가입 성공")
    void registerSuccess() {
        // Given : 중복된 이메일 없음
        when(userRepository.findByEmail(validRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When : 회원가입
        User result = userService.register(validRequest);

        // Then : 가입된 사용자 정보 확인
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(validRequest.getEmail());
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void registerFail_DuplicateEmail() {
        // Given
        when(userRepository.findByEmail(duplicateRequest.getEmail()))
            .thenReturn(Optional.of(new User(duplicateRequest.getEmail(), "encodedPassword")));

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> userService.register(duplicateRequest));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EMAIL_ALREADY_EXISTS);
        assertThat(exception.getMessage()).isEqualTo("이미 존재하는 이메일입니다.");
    }

}