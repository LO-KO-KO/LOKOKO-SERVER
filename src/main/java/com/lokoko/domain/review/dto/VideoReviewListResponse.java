package com.lokoko.domain.review.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record VideoReviewListResponse(
        String searchQuery,
        String parentCategoryName,
        List<VideoReviewResponse> reviews,
        PageableResponse pageInfo
) {
}
