package com.lokoko.global.auth.exception;


import com.lokoko.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AdminPermissionRequiredException extends BaseException {

    public AdminPermissionRequiredException() {
        super(HttpStatus.FORBIDDEN, ErrorMessage.ADMIN_PERMISSION_REQUIRED.getMessage());
    }
}
