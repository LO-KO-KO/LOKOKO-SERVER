package com.lokoko.domain.review.dto.request;

import com.lokoko.domain.review.entity.enums.Rating;

public record RatingCount(
        Long productId,
        Rating rating,
        Long count
) {
}
