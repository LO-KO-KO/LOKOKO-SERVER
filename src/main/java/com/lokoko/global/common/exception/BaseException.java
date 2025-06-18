package com.lokoko.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {
    private final HttpStatus status;

    /**
     * Constructs a new BaseException with the specified HTTP status and detail message.
     *
     * @param status  the HTTP status associated with this exception
     * @param message the detail message explaining the reason for the exception
     */
    public BaseException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }
}
