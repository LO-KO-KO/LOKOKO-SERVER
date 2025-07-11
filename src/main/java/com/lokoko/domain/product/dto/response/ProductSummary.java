package com.lokoko.domain.product.dto.response;

public record ProductSummary(
        String imageUrl,
        Long reviewCount,
        Double avgRating
) {
}
