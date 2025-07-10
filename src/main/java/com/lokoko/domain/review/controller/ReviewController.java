package com.lokoko.domain.review.controller;

import com.lokoko.domain.review.controller.enums.ResponseMessage;
import com.lokoko.domain.review.dto.request.ReviewMediaRequest;
import com.lokoko.domain.review.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.review.dto.request.ReviewRequest;
import com.lokoko.domain.review.dto.response.ReviewMediaResponse;
import com.lokoko.domain.review.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.review.dto.response.ReviewResponse;
import com.lokoko.domain.review.service.ReviewService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/receipt")
    public ApiResponse<ReviewReceiptResponse> createReceiptPresignedUrl(
            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ReviewReceiptRequest request) {
        ReviewReceiptResponse response = reviewService.createReceiptPresignedUrl(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_RECEIPT_PRESIGNED_URL_SUCCESS.getMessage(), response);
    }


    @PostMapping("/media")
    public ApiResponse<ReviewMediaResponse> createMediaPresignedUrl(

            @Parameter(hidden = true) @CurrentUser Long userId,
            @RequestBody @Valid ReviewMediaRequest request) {
        ReviewMediaResponse response = reviewService.createMediaPresignedUrl(userId, request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_MEDIA_PRESIGNED_URL_SUCCESS.getMessage(), response);
    }


    @PostMapping("/{productId}")
    public ApiResponse<ReviewResponse> createReceipt(
            @PathVariable Long productId,
            @CurrentUser Long userId,
            @RequestBody @Valid ReviewRequest request
    ) {
        ReviewResponse response = reviewService.createReview(productId, userId, request);
        return ApiResponse.success(
                HttpStatus.OK,
                ResponseMessage.REVIEW_UPLOAD_SUCCESS.getMessage(),
                response
        );
    }
}
