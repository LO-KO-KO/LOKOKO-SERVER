package com.lokoko.domain.video.repository;

import com.lokoko.domain.video.entity.ReviewVideo;

import java.util.List;

public interface ReviewVideoRepositoryCustom {
    List<ReviewVideo> findMainVideoReviewSorted();
}
