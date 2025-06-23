package com.lokoko.global.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    OAUTH_ERROR("OAuth 인증에 실패했습니다.");

    private final String message;
}
