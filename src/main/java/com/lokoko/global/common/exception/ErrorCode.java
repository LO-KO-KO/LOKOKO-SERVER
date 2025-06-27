package com.lokoko.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    JSON_PROCESSING(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 처리 중 문제가 발생했습니다."),

    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "잘못된 인자입니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "잘못된 JSON 형식의 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),

    // Product 관련
    SUBCATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 서브카테고리입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 제품을 찾을 수 없습니다.");


    private final HttpStatus status;
    private final String message;
}
