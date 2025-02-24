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
    TOKEN_EXPIRED(UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "잘못된 JWT 서명입니다."),

    // 사용자 관련 예외
    MEMBER_NOT_FOUND(NOT_FOUND, "회원을 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(CONFLICT, "이미 존재하는 이메일입니다."),
    INVALID_CREDENTIALS(UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    // 상품 관련 예외
    PRODUCT_NOT_FOUND(NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_CANNOT_BE_DELETED(CONFLICT, "이미 주문된 상품은 삭제할 수 없습니다."),
    PRODUCT_ALREADY_ACTIVE(BAD_REQUEST, "이미 활성화된 상품입니다."),

    // 상품 옵션 관련 예외
    OPTION_NOT_FOUND(NOT_FOUND, "상품 옵션을 찾을 수 없습니다."),
    OPTION_CANNOT_BE_UPDATED(CONFLICT, "이 옵션은 수정할 수 없습니다."),
    OPTION_CANNOT_BE_DELETED(CONFLICT, "이 옵션은 삭제할 수 없습니다."),
    OPTION_CANNOT_BE_ACTIVATED(BAD_REQUEST, "비활성화된 옵션을 활성화할 수 없습니다."), // 비활성화된 옵션이 특정 조건(예: 최대 개수 초과)으로 인해 활성화 불가능
    OPTION_LIMIT_EXCEEDED(BAD_REQUEST, "활성화된 옵션 개수는 최대 3개까지 추가할 수 있습니다."),
    OPTION_CANNOT_HAVE_DETAILS(HttpStatus.BAD_REQUEST, "입력형 옵션에는 상세 옵션을 추가할 수 없습니다."),
    OPTION_MUST_HAVE_DETAILS(HttpStatus.BAD_REQUEST, "선택형 옵션에는 최소 하나 이상의 상세 옵션이 필요합니다."),

    // 상품 상세 옵션 관련 예외
    OPTION_DETAIL_NOT_FOUND(NOT_FOUND, "상품 상세 옵션을 찾을 수 없습니다."),
    OPTION_DETAIL_CANNOT_BE_UPDATED(CONFLICT, "이 상세 옵션은 수정할 수 없습니다."),
    OPTION_DETAIL_CANNOT_BE_DELETED(CONFLICT, "이 상세 옵션은 삭제할 수 없습니다."),
    OPTION_DETAIL_CANNOT_BE_ACTIVATED(BAD_REQUEST, "비활성화된 옵션의 상세 옵션은 활성화할 수 없습니다."),

    // 기타 예외
    INVALID_REQUEST(BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String message; // 에러 메시지

}
