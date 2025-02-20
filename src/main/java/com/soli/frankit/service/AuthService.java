package com.soli.frankit.service;

import com.soli.frankit.dto.LoginRequest;
import com.soli.frankit.dto.TokenResponse;
import com.soli.frankit.entity.User;
import com.soli.frankit.exception.CustomException;
import com.soli.frankit.exception.ErrorCode;
import com.soli.frankit.repository.UserRepository;
import com.soli.frankit.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * packageName  com.soli.frankit.service
 * fileName     AuthService
 * author       eumsoli
 * date         2025-02-20
 * description  로그인 및 JWT 발급을 담당하는 서비스 클래스
 */

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인
     *
     * @param request 로그인 요청 DTO
     * @return JWT 토큰
     * @throws CustomException(ErrorCode.MEMBER_NOT_FOUND) 사용자가 조회되지 않는 경우 예외 발생
     * @throws CustomException(ErrorCode.INVALID_CREDENTIALS) 비밀번호가 틀린 경우 예외 발생
     */
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String token = jwtTokenProvider.createToken(user.getEmail());

        return new TokenResponse(token);
    }

}
