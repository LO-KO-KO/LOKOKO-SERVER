package com.lokoko.domain.review.controller;

import com.lokoko.domain.review.controller.enums.ResponseMessage;
import com.lokoko.domain.review.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.review.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.review.service.ReviewService;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ApiResponse<ReviewReceiptResponse> createReceiptPresignedUrl(@RequestBody ReviewReceiptRequest request) {
        ReviewReceiptResponse response = reviewService.createReceiptPresignedUrl(request);

        return ApiResponse.success(HttpStatus.OK, ResponseMessage.REVIEW_RECEIPT_PRESIGNED_URL_SUCCESS.getMessage(), response);

    }
}
