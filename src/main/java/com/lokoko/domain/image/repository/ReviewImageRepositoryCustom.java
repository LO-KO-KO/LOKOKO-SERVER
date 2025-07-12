package com.lokoko.domain.image.repository;

import com.lokoko.domain.image.entity.ReviewImage;

import java.util.List;

public interface ReviewImageRepositoryCustom {
    List<ReviewImage> findMainImageReviewSorted();
}