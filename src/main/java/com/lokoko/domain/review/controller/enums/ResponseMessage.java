package com.lokoko.domain.review.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    REVIEW_RECEIPT_PRESIGNED_URL_SUCCESS("영수증 사진의 Presigned Url이 성공적으로 발급되었습니다.");

    private final String message;
}

