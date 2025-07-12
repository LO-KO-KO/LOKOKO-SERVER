package com.lokoko.domain.review.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    REVIEW_RECEIPT_PRESIGNED_URL_SUCCESS("영수증 사진의 Presigned Url이 성공적으로 발급되었습니다."),
    REVIEW_MEDIA_PRESIGNED_URL_SUCCESS("리뷰 사진의 Presigned Url이 성공적으로 발급되었습니다."),
    REVIEW_UPLOAD_SUCCESS("리뷰가 성공적으로 작성되었습니다."),
    MAIN_REVIEW_IMAGE_SUCCESS("메인페이지 상세 리뷰 조회에 성공했습니다.");

    private final String message;
}

