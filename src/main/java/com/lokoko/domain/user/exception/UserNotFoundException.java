package com.lokoko.domain.user.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.getMessage());
    }
}
