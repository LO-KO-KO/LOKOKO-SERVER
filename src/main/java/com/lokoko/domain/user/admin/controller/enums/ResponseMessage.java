package com.lokoko.domain.user.admin.controller.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {
    ADMIN_REVIEW_DELETE_SUCCESS("어드민 리뷰 삭제에 성공했습니다");

    private final String message;
}
