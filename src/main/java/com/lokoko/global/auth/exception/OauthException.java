package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OauthException extends BaseException {

    public OauthException() {
        this(ErrorMessage.OAUTH_ERROR);
    }

    public OauthException(ErrorMessage errorMessage) {
        super(HttpStatus.UNAUTHORIZED, errorMessage.getMessage());
    }
}
