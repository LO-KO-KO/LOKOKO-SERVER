package com.lokoko.domain.review.service;

import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.ImageReviewListResponse;
import com.lokoko.domain.review.dto.ImageReviewResponse;
import com.lokoko.domain.review.dto.VideoReviewListResponse;
import com.lokoko.domain.review.dto.VideoReviewResponse;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewReadService {

    private final ReviewRepository reviewRepository;

    // 카테고리별 영상 리뷰 조회
    public VideoReviewListResponse searchVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                SubCategory subCategory,
                                                                int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Slice<VideoReviewResponse> videoReviews = (subCategory == null)
                ? reviewRepository.findVideoReviewsByCategory(middleCategory, pageable)
                : reviewRepository.findVideoReviewsByCategory(middleCategory, subCategory, pageable);

        return new VideoReviewListResponse(videoReviews.getContent(), PageableResponse.of(videoReviews));


    }

    // 카테고리 별 사진 리뷰조회
    public ImageReviewListResponse searchImageReviewsByCategory(MiddleCategory middleCategory,
                                                                SubCategory subCategory,
                                                                int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Slice<ImageReviewResponse> imageReviews = (subCategory == null)
                ? reviewRepository.findImageReviewsByCategory(middleCategory, pageable)
                : reviewRepository.findImageReviewsByCategory(middleCategory, subCategory, pageable);

        return new ImageReviewListResponse(imageReviews.getContent(), PageableResponse.of(imageReviews));

    }


}
