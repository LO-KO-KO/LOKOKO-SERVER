package com.lokoko.global.common.controller;

import com.lokoko.domain.image.controller.enums.ResponseMessage;
import com.lokoko.global.common.dto.PresignedUrlResponse;
import com.lokoko.global.common.response.ApiResponse;
import com.lokoko.global.common.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PRESIGNED URL")
@RestController
@RequestMapping("/api/presignedUrl")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3Service;

    @Operation(summary = "Presigned Url 제공받는 API 입니다.")
    @GetMapping
    public ApiResponse<PresignedUrlResponse> getPresignedUrl(
            @RequestParam String fileType
    ) {
        PresignedUrlResponse response = s3Service.generatePresignedUrl(fileType);
        return ApiResponse.success(
                HttpStatus.OK,
                ResponseMessage.PRESIGNED_URL_SUCCESS.getMessage(),
                response
        );
    }
}