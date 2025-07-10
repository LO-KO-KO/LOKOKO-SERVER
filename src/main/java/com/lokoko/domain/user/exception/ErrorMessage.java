package com.lokoko.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    NOT_FOUND_USER("해당 ID의 유저가 존재하지 않습니다.");

    private final String message;
}
