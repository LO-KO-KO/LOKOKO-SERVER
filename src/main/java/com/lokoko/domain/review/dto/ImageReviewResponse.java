package com.lokoko.domain.review.dto;

public record ImageReviewResponse(
        Long reviewId,
        int ranking,
        String brandName,
        String productName,
        int likeCount,
        String url
) {
}
