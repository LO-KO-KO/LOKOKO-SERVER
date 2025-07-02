package com.lokoko.domain.product.dto;


public record ProductResponse(
        Long productId, // 제품 id(추후 상세조회를 위해서)
        String imageUrl, // 제품 이미지
        String productName, // 제품 이름
        Long reviewCount, // 리뷰 개수
        Double rating // 별점
) {


}
