package com.lokoko.domain.review.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidMediaTypeException extends BaseException {
    public InvalidMediaTypeException(ErrorMessage errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage.getMessage());
    }
}