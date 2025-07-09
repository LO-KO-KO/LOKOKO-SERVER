package com.lokoko.domain.review.service;

import com.lokoko.domain.product.entity.enums.MiddleCategory;
import com.lokoko.domain.product.entity.enums.SubCategory;
import com.lokoko.domain.review.dto.ImageReviewListResponse;
import com.lokoko.domain.review.dto.ImageReviewResponse;
import com.lokoko.domain.review.dto.ReviewListResponse;
import com.lokoko.domain.review.dto.VideoReviewListResponse;
import com.lokoko.domain.review.dto.VideoReviewResponse;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.global.common.response.PageableResponse;
import com.lokoko.global.kuromoji.service.KuromojiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewReadService {

    private final ReviewRepository reviewRepository;
    private final KuromojiService kuromojiService;

    // 카테고리별 영상 리뷰 조회
    public VideoReviewListResponse searchVideoReviewsByCategory(MiddleCategory middleCategory,
                                                                SubCategory subCategory,
                                                                int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Slice<VideoReviewResponse> videoReviews = (subCategory == null)
                ? reviewRepository.findVideoReviewsByCategory(middleCategory, pageable)
                : reviewRepository.findVideoReviewsByCategory(middleCategory, subCategory, pageable);

        return VideoReviewListResponse.builder()
                .searchQuery(subCategory == null
                        ? middleCategory.getDisplayName()
                        : subCategory.getDisplayName())
                .parentCategoryName(subCategory == null
                        ? middleCategory.getParent().getDisplayName()
                        : subCategory.getMiddleCategory().getParent().getDisplayName())
                .reviews(videoReviews.getContent())
                .pageInfo(PageableResponse.of(videoReviews))
                .build();

    }

    // 카테고리 별 사진 리뷰조회
    public ImageReviewListResponse searchImageReviewsByCategory(MiddleCategory middleCategory,
                                                                SubCategory subCategory,
                                                                int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Slice<ImageReviewResponse> imageReviews = (subCategory == null)
                ? reviewRepository.findImageReviewsByCategory(middleCategory, pageable)
                : reviewRepository.findImageReviewsByCategory(middleCategory, subCategory, pageable);

//        return new ImageReviewListResponse(imageReviews.getContent(), PageableResponse.of(imageReviews));

        return ImageReviewListResponse.builder()
                .searchQuery(subCategory == null
                        ? middleCategory.getDisplayName()
                        : subCategory.getDisplayName())
                .parentCategoryName(subCategory == null
                        ? middleCategory.getParent().getDisplayName()
                        : subCategory.getMiddleCategory().getParent().getDisplayName())
                .reviews(imageReviews.getContent())
                .pageInfo(PageableResponse.of(imageReviews))
                .build();

    }


    public ReviewListResponse<VideoReviewResponse> searchVideoReviewsByKeyword(String keyword, int page, int size) {

        List<String> tokens = kuromojiService.tokenize(keyword);
        Pageable pageable = PageRequest.of(page, size);

        Slice<VideoReviewResponse> videoReviews = reviewRepository.findVideoReviewsByKeyword(tokens,
                pageable);

        return ReviewListResponse.from(keyword, videoReviews);


    }


    public ReviewListResponse<ImageReviewResponse> searchImageReviewsByKeyword(String keyword, int page, int size) {

        List<String> tokens = kuromojiService.tokenize(keyword);
        Pageable pageable = PageRequest.of(page, size);

        Slice<ImageReviewResponse> imageReviews = reviewRepository.findImageReviewsByKeyword(tokens,
                pageable);

        return ReviewListResponse.from(keyword, imageReviews);
    }
}
