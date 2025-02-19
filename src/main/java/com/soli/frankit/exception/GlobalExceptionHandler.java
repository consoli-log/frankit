package com.soli.frankit.exception;

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
    public ResponseEntity<Map<String, String>> handleCustomException(CustomException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }

}
