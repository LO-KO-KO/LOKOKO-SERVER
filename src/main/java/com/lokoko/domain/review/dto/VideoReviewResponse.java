package com.lokoko.domain.review.dto;

public record VideoReviewResponse(
        String reviewId,
        int ranking,
        String brandName,
        String productName,
        int likeCount,
        String url
) {
}
