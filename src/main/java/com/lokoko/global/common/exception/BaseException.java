package com.lokoko.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;

    public BaseException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }
}
