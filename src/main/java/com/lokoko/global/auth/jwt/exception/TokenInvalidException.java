package com.lokoko.global.auth.jwt.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenInvalidException extends BaseException {
    public TokenInvalidException() {
        super(HttpStatus.UNAUTHORIZED, JwtErrorMessage.JWT_TOKEN_INVALID.getMessage());
    }
}
