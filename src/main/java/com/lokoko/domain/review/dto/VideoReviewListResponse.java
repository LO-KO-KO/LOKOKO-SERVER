package com.lokoko.domain.review.dto;

import com.lokoko.global.common.response.PageableResponse;
import java.util.List;

public record VideoReviewListResponse(
        List<VideoReviewResponse> reviews,
        PageableResponse pageInfo
) {
}
