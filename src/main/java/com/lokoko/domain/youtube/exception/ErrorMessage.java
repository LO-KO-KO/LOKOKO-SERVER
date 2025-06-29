package com.lokoko.domain.youtube.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    YOUTUBE_API_CALL_FAILED("YouTube API 호출에 실패했습니다.");

    private final String message;
}
