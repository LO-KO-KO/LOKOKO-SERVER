package com.lokoko.global.kuromoji.exception;


import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class TokenizeFailedException extends BaseException {
    public TokenizeFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.TOKENIZE_FAILED.getMessage());
    }
}
