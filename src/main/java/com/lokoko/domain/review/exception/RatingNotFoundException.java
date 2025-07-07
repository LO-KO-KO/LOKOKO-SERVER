package com.lokoko.domain.review.exception;

import static com.lokoko.domain.review.exception.ErrorMessage.RATING_NOT_FOUND;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RatingNotFoundException extends BaseException {
    public RatingNotFoundException() {
        super(HttpStatus.NOT_FOUND, RATING_NOT_FOUND.getMessage());
    }
}
