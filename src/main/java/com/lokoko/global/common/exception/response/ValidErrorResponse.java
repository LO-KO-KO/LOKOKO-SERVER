package com.lokoko.global.common.exception.response;

public record ValidErrorResponse(
        String errorField,
        String errorMessage,
        Object inputValue
) {

    public static ValidErrorResponse of(String field, String msg, Object value) {
        return new ValidErrorResponse(field, msg, value);
    }
}
