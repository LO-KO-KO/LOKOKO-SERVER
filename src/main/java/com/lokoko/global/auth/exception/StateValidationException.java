package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class StateValidationException extends BaseException {
    public StateValidationException(ErrorMessage errorMessage) {
        super(HttpStatus.BAD_REQUEST, errorMessage.getMessage());
    }

    public static StateValidationException required() {
        return new StateValidationException(ErrorMessage.STATE_PARAMETER_REQUIRED);
    }

    public static StateValidationException invalid() {
        return new StateValidationException(ErrorMessage.STATE_PARAMETER_INVALID);
    }

    public static StateValidationException expired() {
        return new StateValidationException(ErrorMessage.STATE_PARAMETER_EXPIRED);
    }
}
