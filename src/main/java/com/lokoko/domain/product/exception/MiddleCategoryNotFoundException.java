package com.lokoko.domain.product.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MiddleCategoryNotFoundException extends BaseException {
    public MiddleCategoryNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.MIDDLECATEGORY_NOT_FOUND.getMessage());
    }
}
