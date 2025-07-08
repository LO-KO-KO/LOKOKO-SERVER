package com.lokoko.domain.image.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    FILE_TYPE_NOT_SUPPORTED("지원하지 않는 파일 형식입니다.");

    private final String message;
}
