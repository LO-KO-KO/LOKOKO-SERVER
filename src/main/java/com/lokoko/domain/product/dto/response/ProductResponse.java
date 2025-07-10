package com.lokoko.domain.product.dto.response;


import com.lokoko.domain.product.entity.Product;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record ProductResponse(
        Long productId, // 제품 id(추후 상세조회를 위해서)
        List<String> imageUrls, // 제품 이미지
        String productName,// 제품 이름
        String brandName, // 브랜드 이름
        String unit, // 제품 단위
        Long reviewCount, // 리뷰 개수
        Double rating, // 별점
        Boolean isLiked // 좋아요 여부
) {
    public static ProductResponse of(
            Product product,
            ProductSummary summary,
            boolean isLiked
    ) {
        List<String> images = Optional.ofNullable(summary.imageUrl())
                .filter(u -> !u.isBlank())
                .map(u -> u.contains(",")
                        ? Arrays.stream(u.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList()
                        : List.of(u)
                )
                .orElseGet(List::of);

        return new ProductResponse(
                product.getId(),
                images,
                product.getProductName(),
                product.getBrandName(),
                product.getUnit(),
                summary.reviewCount(),
                summary.avgRating(),
                isLiked
        );
    }
}
