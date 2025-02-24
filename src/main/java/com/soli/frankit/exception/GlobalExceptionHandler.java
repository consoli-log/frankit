package com.soli.frankit.exception;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName  com.soli.frankit.exception
 * fileName     GlobalExceptionHandler
 * author       eumsoli
 * date         2025-02-19
 * description  전역 예외를 처리하는 핸들러
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     *
     * @param e 발생한 CustomException 객체
     * @return HTTP 상태 코드와 예외 메시지를 포함한 응답 반환
     */
    @ExceptionHandler(CustomException.class)
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "데이터 충돌"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    /**
     * MethodArgumentNotValidException 처리 (DTO Validation 실패)
     *
     * @param e 발생한 MethodArgumentNotValidException 객체
     * @return HTTP 400 Bad Request 필드별 응답 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errorResponse = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errorResponse.put(error.getField(), error.getDefaultMessage())); // 필드별 에러 메시지 저장
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



}
