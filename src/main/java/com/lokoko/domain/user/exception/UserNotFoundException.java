package com.lokoko.domain.user.exception;

import com.lokoko.global.common.exception.BaseException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(NOT_FOUND, ErrorMessage.NOT_FOUND_USER.getMessage());
    }
}