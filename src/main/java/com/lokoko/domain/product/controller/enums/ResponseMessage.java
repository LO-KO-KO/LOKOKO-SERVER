package com.lokoko.domain.product.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    PRODUCT_CRAWL_SUCCESS("상품 크롤링에 성공했습니다."),
    CATEGORY_SEARCH_SUCCESS("카테고리 별 제품 검색에 성공했습니다."),
    CATEGORY_LIST_SUCCESS("카테고리 리스트 반환에 성공했습니다.");

    private final String message;
}
