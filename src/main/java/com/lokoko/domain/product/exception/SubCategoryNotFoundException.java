package com.lokoko.domain.product.exception;

import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

//SubCategoryNotFoundException 은, 서브카테고리의 number 로 enum values 를 통해 검색을 했을때
// 해당하는 서브 카테고리가 없는 경우 던지는 예외입니다.
public class SubCategoryNotFoundException extends BaseException {

    public SubCategoryNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
