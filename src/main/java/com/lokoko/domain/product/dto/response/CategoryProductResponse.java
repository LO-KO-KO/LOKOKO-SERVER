package com.lokoko.domain.product.dto.response;

import java.util.List;

// 카테고리 검색의 결과를 포함하고 있는 DTO 입니다.
public record CategoryProductResponse(
        // 검색창에는 사용자가 검색한 서브 카테고리 이름
        String searchQuery,
        // 사용자가 선택한 MainCategory
        String mainCategory,
        // 사용자가 선택한 SubCategory
        String subCategory,
        // 제품 검색 결과 개수
        int resultCount,
        // 검색 결과 제품 리스트
        List<ProductResponse> products
) {
}

