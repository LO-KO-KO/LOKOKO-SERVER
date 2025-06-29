package com.lokoko.domain.youtube.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class YoutubeApiException extends BaseException {
    public YoutubeApiException() {
        super(HttpStatus.BAD_GATEWAY, ErrorMessage.YOUTUBE_API_CALL_FAILED.getMessage());
    }
}
