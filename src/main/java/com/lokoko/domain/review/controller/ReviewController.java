package com.lokoko.domain.review.controller;

import com.lokoko.domain.review.controller.enums.ResponseMessage;
import com.lokoko.domain.review.dto.request.ReviewMediaRequest;
import com.lokoko.domain.review.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.review.dto.request.ReviewRequest;
import com.lokoko.domain.review.dto.response.MainImageReviewResponse;
import com.lokoko.domain.review.dto.response.MainVideoReviewResponse;
import com.lokoko.domain.review.dto.response.ReviewMediaResponse;
import com.lokoko.domain.review.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.review.dto.response.ReviewResponse;
import com.lokoko.domain.review.service.ReviewService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "REVIEW")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "영수증 presignedUrl 발급")
    @PostMapping("/receipt")
    public ApiResponse<ReviewReceiptResponse> createReceiptPresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ReviewReceiptRequest request) {
        ReviewReceiptResponse response = reviewService.createReceiptPresignedUrl(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_RECEIPT_PRESIGNED_URL_SUCCESS.getMessage(), response);
    }


    @Operation(summary = "사진 또는 영상 presignedUrl 발급")
    @PostMapping("/media")
    public ApiResponse<ReviewMediaResponse> createMediaPresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ReviewMediaRequest request) {
        ReviewMediaResponse response = reviewService.createMediaPresignedUrl(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_MEDIA_PRESIGNED_URL_SUCCESS.getMessage(), response);
    }

    @Operation(summary = "리뷰 작성")
    @PostMapping("/{productId}")
    public ApiResponse<ReviewResponse> createReview(
            @PathVariable Long productId,
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ReviewRequest request
    ) {
        ReviewResponse response = reviewService.createReview(productId, userId, request);
        return ApiResponse.success(
                HttpStatus.OK,
                ResponseMessage.REVIEW_UPLOAD_SUCCESS.getMessage(),
                response
        );
    }

    @GetMapping("/image")
    public ApiResponse<MainImageReviewResponse> getMainImageReviews() {
        MainImageReviewResponse response = reviewService.getMainImageReview();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.MAIN_REVIEW_IMAGE_SUCCESS.getMessage(), response);
    }

    @GetMapping("/video")
    public ApiResponse<MainVideoReviewResponse> getMainVideoReviews() {
        MainVideoReviewResponse response = reviewService.getMainVideoReview();
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.MAIN_REVIEW_VIDEO_SUCCESS.getMessage(), response);
    }
}
