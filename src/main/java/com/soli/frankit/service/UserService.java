package com.soli.frankit.service;

import com.soli.frankit.dto.RegisterRequest;
import com.soli.frankit.entity.User;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * packageName  com.soli.frankit.service
 * fileName     UserService
 * author       eumsoli
 * date         2025-02-19
 * description  회원가입 및 사용자 관리를 담당하는 서비스 클래스
 */

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     *
     * @param request 회원가입 요청 DTO
     * @return 가입된 사용자 정보
     * @throws CustomException(ErrorCode.EMAIL_ALREADY_EXISTS) 중복 이메일 예외 발생
     */
    public User register(RegisterRequest request) {
        // 이메일 중복 검사
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 저장
        User user = User.builder()
                        .email(request.getEmail())
                        .password(encodedPassword)
                        .build();

        return userRepository.save(user);
    }

}

