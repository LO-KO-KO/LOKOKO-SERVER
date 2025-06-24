package com.lokoko.global.auth.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class OauthException extends BaseException {
    public OauthException() {
        super(HttpStatus.UNAUTHORIZED, ErrorMessage.OAUTH_ERROR.getMessage());
    }
}
