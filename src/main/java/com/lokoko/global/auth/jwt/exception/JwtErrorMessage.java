package com.lokoko.global.auth.jwt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtErrorMessage {
    JWT_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰을 찾을 수 없습니다"),
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 입니다."),

    COOKIE_NOT_FOUND(HttpStatus.BAD_REQUEST, "헤더에 RefreshToken이 없습니다."),
    REDIS_NOT_FOUND(HttpStatus.NOT_FOUND, "Redis에서 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
