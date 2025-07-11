package com.lokoko.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    USER_NOT_FOUND("존재하지 않는 유저입니다");

    private final String message;
}
