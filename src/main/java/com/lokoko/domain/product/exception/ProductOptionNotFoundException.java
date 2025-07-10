package com.lokoko.domain.product.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductOptionNotFoundException extends BaseException {
    public ProductOptionNotFoundException() {
        super(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_OPTION_NOT_FOUND.getMessage());
    }
}
