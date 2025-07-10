package com.lokoko.domain.product.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductOptionMismatchException extends BaseException {
    public ProductOptionMismatchException() {
        super(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_OPTION_MISMATCH.getMessage());
    }
}
