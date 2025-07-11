package com.lokoko.domain.review.dto.response;

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
    public static <T> ReviewListResponse<T> from(String keyword, Slice<T> reviews) {
        return new ReviewListResponse(
                keyword,
                reviews.getContent(),
                PageableResponse.of(reviews)
        );
    }
}
