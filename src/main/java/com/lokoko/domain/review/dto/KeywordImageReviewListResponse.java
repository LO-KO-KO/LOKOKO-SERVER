package com.lokoko.domain.review.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;
import org.springframework.data.domain.Slice;

public record KeywordImageReviewListResponse(
        String searchQuery,
        List<ImageReviewResponse> reviews,
        PageableResponse pageInfo
) {
    public static KeywordImageReviewListResponse from(String keyword, Slice<ImageReviewResponse> reviews) {
        return new KeywordImageReviewListResponse(
                keyword,
                reviews.getContent(),
                PageableResponse.of(reviews)
        );
    }
}
