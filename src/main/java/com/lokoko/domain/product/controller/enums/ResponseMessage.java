package com.lokoko.domain.product.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    PRODUCT_CRAWL_SUCCESS("상품 크롤링에 성공했습니다.");

    private final String message;
}
