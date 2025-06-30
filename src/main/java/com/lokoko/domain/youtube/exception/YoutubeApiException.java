package com.lokoko.domain.youtube.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class YoutubeApiException extends BaseException {
    public YoutubeApiException(ErrorMessage errorMessage) {
        super(HttpStatus.BAD_GATEWAY, errorMessage.getMessage());
    }
}
