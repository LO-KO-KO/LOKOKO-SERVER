package com.lokoko.domain.review.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record ImageReviewListResponse(
        String searchQuery,
        String parentCategoryName,
        List<ImageReviewResponse> reviews,
        PageableResponse pageInfo
) {
}
