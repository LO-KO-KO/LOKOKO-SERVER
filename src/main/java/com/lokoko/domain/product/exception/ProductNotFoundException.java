package com.lokoko.domain.product.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_NOT_FOUND.getMessage());
    }
}
