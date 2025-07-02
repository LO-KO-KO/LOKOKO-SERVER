package com.lokoko.global.auth.jwt.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED, JwtErrorMessage.JWT_TOKEN_EXPIRED.getMessage());
    }
}

