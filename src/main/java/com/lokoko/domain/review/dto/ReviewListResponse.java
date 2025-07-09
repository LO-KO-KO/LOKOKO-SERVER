package com.lokoko.domain.review.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Slice;

@Builder
public record ReviewListResponse<T>(
        String searchQuery,
        List<T> reviews,
        PageableResponse pageInfo

) {
    public static <T> ReviewListResponse<T> from(String keyword, Slice<T> videoReviews) {
        return new ReviewListResponse(
                keyword,
                videoReviews.getContent(),
                PageableResponse.of(videoReviews)
        );
    }
}
