package com.lokoko.domain.review.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    RATING_NOT_FOUND("존재하지 않는 평점입니다."),
    MISSING_MEDIA_TYPE("searchType이 REVIEW일 때 mediaType은 필수입니다."),
    INVALID_MEDIA_TYPE_FORMAT("mediaType은 'video/xxx' 또는 'image/xxx' 형식이어야 합니다."),
    INVALID_MEDIA_TYPE_PLURAL("mediaType은 'videos' 또는 'images'가 아닌 'video/' 또는 'image/'로 시작해야 합니다."),
    UNSUPPORTED_MEDIA_TYPE("지원하지 않는 파일 형식입니다.");

    private final String message;
}
