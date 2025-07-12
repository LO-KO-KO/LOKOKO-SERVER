package com.lokoko.domain.review.dto.response;

public record VideoReviewResponse(
        Long reviewId,
        String brandName,
        String productName,
        int likeCount,
        String url
) {
}
