package com.lokoko.domain.product.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    LIKE_TOGGLE_SUCCESS("좋아요 토글에 성공했습니다.");

    private final String message;
}
