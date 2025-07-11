package com.lokoko.domain.user.admin.controller;

import com.lokoko.domain.user.admin.controller.enums.ResponseMessage;
import com.lokoko.domain.user.admin.service.AdminReviewService;
import com.lokoko.global.auth.annotation.CurrentUser;
import com.lokoko.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ADMIN")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminReviewService adminReviewService;

    @DeleteMapping("/reviews/{reviewId}")
    public ApiResponse<Void> deleteReviewByAdmin(@CurrentUser Long userId, @PathVariable Long reviewId) {
        adminReviewService.deleteReview(userId, reviewId);
        return ApiResponse.success(HttpStatus.OK, ResponseMessage.ADMIN_REVIEW_DELETE_SUCCESS.getMessage());
    }
}
