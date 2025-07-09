package com.lokoko.domain.review.repository;

import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.ImageReviewResponse;
import com.lokoko.domain.review.dto.VideoReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {

    Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory, SubCategory subCategory,
                                                          Pageable pageable
    );

    Slice<VideoReviewResponse> findVideoReviewsByCategory(MiddleCategory middleCategory,
                                                          Pageable pageable
    );

    Slice<ImageReviewResponse> findImageReviewsByCategory(MiddleCategory middleCategory, SubCategory subCategory,
                                                          Pageable pageable
    );

    Slice<ImageReviewResponse> findImageReviewsByCategory(MiddleCategory middleCategory,
                                                          Pageable pageable
    );

}
