package com.lokoko.domain.review.exception;

import static com.lokoko.domain.review.exception.ErrorMessage.MISSING_MEDIA_TYPE;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MissingMediaTypeException extends BaseException {
    public MissingMediaTypeException() {
        super(HttpStatus.BAD_REQUEST, MISSING_MEDIA_TYPE.getMessage());
    }
}
