package com.soli.frankit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * packageName  com.soli.frankit.exception
 * fileName     ErrorCode
 * author       eumsoli
 * date         2025-02-19
 * description  프로젝트에서 사용되는 예외 코드와 메시지를 정의하는 Enum
 */

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련 예외
    TOKEN_EXPIRED(UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "잘못된 JWT 서명입니다."),

    // 사용자 관련 예외
    MEMBER_NOT_FOUND(NOT_FOUND, "회원을 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(CONFLICT, "이미 존재하는 이메일입니다."),

    // 기타 예외
    INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
    VALIDATION_FAILED(BAD_REQUEST, "유효성 검사에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String message; // 에러 메시지

}
