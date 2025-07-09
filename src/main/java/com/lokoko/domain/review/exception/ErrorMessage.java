package com.lokoko.domain.review.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    RATING_NOT_FOUND("존재하지 않는 평점입니다."),
    MISSING_MEDIA_TYPE("searchType이 REVIEW일 때 mediaType은 필수입니다.");

    private final String message;
}
