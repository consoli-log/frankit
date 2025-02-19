package com.soli.frankit.controller;

import com.soli.frankit.dto.RegisterRequest;
import com.soli.frankit.entity.User;
import com.soli.frankit.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName  com.soli.frankit.controller
 * fileName     UserController
 * author       eumsoli
 * date         2025-02-19
 * description  회원가입 및 사용자 관리를 위한 컨트롤러
 */

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     *
     * @param request 회원가입 요청 DTO
     * @return 가입된 사용자 정보
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(user);
    }

}
