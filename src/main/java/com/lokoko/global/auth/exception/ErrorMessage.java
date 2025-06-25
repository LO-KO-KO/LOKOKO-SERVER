package com.lokoko.global.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    OAUTH_ERROR("OAuth 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED.value()),

    // LINE 관련
    LINE_TOKEN_REQUEST_FAILED("LINE 토큰 요청에 실패했습니다.", HttpStatus.BAD_GATEWAY.value()),
    LINE_PROFILE_FETCH_FAILED("LINE 프로필 조회에 실패했습니다.", HttpStatus.BAD_GATEWAY.value()),

    // state 관련
    STATE_PARAMETER_REQUIRED("State 매개변수가 필요합니다.", HttpStatus.BAD_REQUEST.value()),
    STATE_PARAMETER_INVALID("유효하지 않거나 만료된 State 매개변수입니다.", HttpStatus.BAD_REQUEST.value()),
    STATE_PARAMETER_EXPIRED("State 매개변수가 만료되었습니다.", HttpStatus.BAD_REQUEST.value());

    private final String message;
    private final int httpStatus;
}
