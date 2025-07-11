package com.lokoko.domain.review.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PresignedUrlParsingException extends BaseException {
    public PresignedUrlParsingException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_PRESIGNED_URL.getMessage());
    }
}
