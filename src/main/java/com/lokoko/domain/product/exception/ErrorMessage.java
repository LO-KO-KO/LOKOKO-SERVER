package com.lokoko.domain.product.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    PRODUCT_NOT_FOUND("존재하지 않는 상품입니다"),

    // Product 관련
    SUBCATEGORY_NOT_FOUND("존재하지 않는 카테고리입니다."),
    MIDDLECATEGORY_NOT_FOUND("존재하지 않는 중간 카테고리입니다.");

    private final String message;
}
