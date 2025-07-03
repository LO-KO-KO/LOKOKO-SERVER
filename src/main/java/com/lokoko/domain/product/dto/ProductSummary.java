package com.lokoko.domain.product.dto;

import java.math.BigDecimal;

public record ProductSummary(
        String imageUrl,
        Long reviewCount,
        BigDecimal avgRating
) {
}
