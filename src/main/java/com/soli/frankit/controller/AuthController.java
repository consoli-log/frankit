package com.soli.frankit.controller;

import com.soli.frankit.dto.LoginRequest;
import com.soli.frankit.dto.TokenResponse;
import com.soli.frankit.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName  : com.soli.frankit.controller
 * fileName     : AuthController
 * author       : eumsoli
 * date         : 2025-02-20
 * description  : 로그인 및 인증을 처리하는 컨트롤러
 */

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 API
     *
     * @param  request 로그인 요청 DTO
     * @return JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
