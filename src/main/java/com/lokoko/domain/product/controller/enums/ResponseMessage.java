package com.lokoko.domain.product.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    PRODUCT_CRAWL_SUCCESS("상품 크롤링에 성공했습니다."),
    CATEGORY_SEARCH_SUCCESS("카테고리 별 제품 검색에 성공했습니다."),
    CATEGORY_LIST_SUCCESS("카테고리 리스트 반환에 성공했습니다."),
    PRODUCT_CRAWL_NEW_SUCCESS("신상품 크롤링에 성공했습니다."),
    PRODUCT_OPTION_SUCCESS("상품 옵션 크롤링에 성공했습니다."),
    CATEGORY_NEW_LIST_SUCCESS("신상품 카테고리 리스트 반환에 성공했습니다."),
    PRODUCT_DETAIL_SUCCESS("상세조회 (제품관련) 조회에 성공했습니다."),
    PRODUCT_YOUTUBE_DETAIL_SUCCESS("상세조회 (유튜브) 조회에 성공했습니다."),
    NAME_BRAND_SEARCH_SUCCESS("상품명 / 브랜드명 제품 검색에 성공했습니다."),
    PRODUCT_MIGRATION_SUCCESS("제품 검색 필드 업데이트 완료"),
    CATEGORY_POPULAR_LIST_SUCCESS("카테고리 인기 제품 리스트 반환에 성공했습니다.");

    private final String message;
}
