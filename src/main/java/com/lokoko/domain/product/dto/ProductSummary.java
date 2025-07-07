package com.lokoko.domain.product.dto;

public record ProductSummary(
        String imageUrl,
        Long reviewCount,
        Double avgRating
) {
}
