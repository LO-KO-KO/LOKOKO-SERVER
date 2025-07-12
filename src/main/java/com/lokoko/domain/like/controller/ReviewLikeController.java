package com.lokoko.domain.like.controller;

import static com.lokoko.domain.like.controller.enums.ResponseMessage.REVIEW_LIKE_TOGGLE_SUCCESS;

import com.lokoko.domain.like.dto.response.ReviewLikeResponse;
import com.lokoko.domain.like.service.ReviewLikeService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "REVIEW LIKE")
@RestController
@RequestMapping("/api/likes/reviews/{reviewId}")
@RequiredArgsConstructor
public class ReviewLikeController {
    private final ReviewLikeService reviewLikeService;

    @Operation(summary = "리뷰 좋아요 토글 (추가/취소)")
    @PostMapping
    public ApiResponse<ReviewLikeResponse> toggleLike(@PathVariable final Long reviewId,
                                                      @Parameter(hidden = true) @CurrentUser Long userId) {
        long likeCount = reviewLikeService.toggleReviewLike(reviewId, userId);

        return ApiResponse.success(HttpStatus.OK, REVIEW_LIKE_TOGGLE_SUCCESS.getMessage(),
                new ReviewLikeResponse(likeCount));
    }
}
