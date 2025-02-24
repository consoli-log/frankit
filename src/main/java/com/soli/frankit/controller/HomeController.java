package com.soli.frankit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName  : com.soli.frankit.controller
 * fileName     : HomeController
 * author       : eumsoli
 * date         : 2025-02-24
 * description  : 홈 컨트롤러 (기본 페이지)
 */

@RestController
@Tag(name = "홈 API", description = "API 기본 엔드포인트")
public class HomeController {

    /**
     * 기본 홈 엔드포인트
     *
     * @return API 환영 메시지
     */
    @GetMapping("/")
    @Operation(summary = "API 홈", description = "API의 기본 엔드포인트입니다. 기본적인 정보와 Swagger 링크를 제공합니다.")
    public String home() {
        return "Welcome to the API! Visit <a href='/swagger-ui.html'>Swagger UI</a> for API documentation.";
    }
}
