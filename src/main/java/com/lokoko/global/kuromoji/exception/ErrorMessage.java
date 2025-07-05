package com.lokoko.global.kuromoji.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    TOKENIZE_FAILED("토큰화에 실패하였습니다.");

    private final String message;
}
