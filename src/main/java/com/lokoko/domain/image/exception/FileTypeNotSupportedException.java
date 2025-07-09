package com.lokoko.domain.image.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

import static com.lokoko.domain.image.exception.ErrorMessage.FILE_TYPE_NOT_SUPPORTED;


public class FileTypeNotSupportedException extends BaseException {
    public FileTypeNotSupportedException() {
        super(HttpStatus.BAD_REQUEST, FILE_TYPE_NOT_SUPPORTED.getMessage());
    }
}
