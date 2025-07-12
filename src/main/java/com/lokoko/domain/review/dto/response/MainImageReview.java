package com.lokoko.domain.review.dto.response;

import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.review.entity.Review;

public record MainImageReview(
        Long reviewId,
        String brandName,
        String productName,
        int likeCount,
        int rank,
        String reviewImage
) {

    public static MainImageReview from(ReviewImage reviewImage, int rank) {
        Review review = reviewImage.getReview();

        return new MainImageReview(
                review.getId(),
                review.getProduct().getBrandName(),
                review.getProduct().getProductName(),
                review.getLikeCount(),
                rank,
                reviewImage.getMediaFile().getFileUrl()
        );
    }
}


