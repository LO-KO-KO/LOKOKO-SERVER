package com.lokoko.domain.product.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {

    public ProductNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
