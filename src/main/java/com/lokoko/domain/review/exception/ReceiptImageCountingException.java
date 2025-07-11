package com.lokoko.domain.review.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ReceiptImageCountingException extends BaseException {
    public ReceiptImageCountingException(ErrorMessage errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage.getMessage());
    }
}