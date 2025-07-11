package com.lokoko.domain.review.dto.response;

import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.video.entity.ReviewVideo;

public record MainVideoReview(
        Long reviewId,
        String brandName,
        String productName,
        int likeCount,
        int rank,
        String reviewVideo
) {

    public static MainVideoReview from(ReviewVideo reviewVideo, int rank) {
        Review review = reviewVideo.getReview();

        return new MainVideoReview(
                review.getId(),
                review.getProduct().getBrandName(),
                review.getProduct().getProductName(),
                review.getLikeCount(),
                rank,
                reviewVideo.getMediaFile().getFileUrl()
        );
    }
}

