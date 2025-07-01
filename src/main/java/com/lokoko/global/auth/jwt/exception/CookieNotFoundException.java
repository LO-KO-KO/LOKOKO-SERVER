package com.lokoko.global.auth.jwt.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CookieNotFoundException extends BaseException {
    public CookieNotFoundException() {
        super(HttpStatus.BAD_REQUEST, JwtErrorMessage.COOKIE_NOT_FOUND.getMessage());
    }
}
