package com.lokoko.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    NOT_FOUND_USER("존재하지 않는 유저입니다.")
    ;

    private final String message;
}
