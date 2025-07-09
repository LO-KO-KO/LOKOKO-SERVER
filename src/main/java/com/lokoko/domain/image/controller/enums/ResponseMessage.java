package com.lokoko.domain.image.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    PRESIGNED_URL_SUCCESS("PresignedUrl이 성공적으로 제공되었습니다.");

    private final String message;
}
