package com.lokoko.domain.product.dto;


import java.util.List;

public record ProductResponse(
        Long productId, // 제품 id(추후 상세조회를 위해서)
        List<String> imageUrls, // 제품 이미지
        String productName,// 제품 이름
        String brandName, // 브랜드 이름
        String unit, // 제품 단위
        Long reviewCount, // 리뷰 개수
        Double rating // 별점
) {
}
