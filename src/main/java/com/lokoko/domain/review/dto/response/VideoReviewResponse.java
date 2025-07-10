package com.lokoko.domain.review.dto.response;

public record VideoReviewResponse(
        Long reviewId,
        int ranking,
        String brandName,
        String productName,
        int likeCount,
        String url
) {
}
