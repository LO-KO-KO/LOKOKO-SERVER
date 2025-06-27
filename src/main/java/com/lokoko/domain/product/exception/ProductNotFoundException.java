package com.lokoko.domain.product.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

//ProductNotFoundException 은, product id 로 제품을 검색했을 때, 해당하는 제품이 없는 경우 던지는 예외입니다.
public class ProductNotFoundException extends BaseException {

    public ProductNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
