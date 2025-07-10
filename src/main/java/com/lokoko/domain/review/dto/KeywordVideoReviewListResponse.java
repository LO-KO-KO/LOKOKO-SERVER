package com.lokoko.domain.review.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record KeywordVideoReviewListResponse(
        String searchQuery,
        List<VideoReviewResponse> reviews,
        PageableResponse pageInfo
) {
    public static KeywordVideoReviewListResponse from(String keyword, Slice<VideoReviewResponse> reviews) {
        return new KeywordVideoReviewListResponse(
                keyword,
                reviews.getContent(),
                PageableResponse.of(reviews)
        );
    }
}
