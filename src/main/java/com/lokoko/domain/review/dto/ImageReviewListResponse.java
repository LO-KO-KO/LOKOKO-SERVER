package com.lokoko.domain.review.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;

public record ImageReviewListResponse(
        List<ImageReviewResponse> reviews,
        PageableResponse pageInfo
) {
}
