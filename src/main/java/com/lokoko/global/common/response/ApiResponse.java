package com.lokoko.global.common.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(
            HttpStatus httpStatus,
            String message,
            T data
    ) {
        return new ApiResponse<>(
                true,
                httpStatus.value(),
                message,
                data
        );
    }

    public static ApiResponse<Void> success(
            HttpStatus httpStatus,
            String message
    ) {
        return success(httpStatus, message, null);
    }

    public static <T> ApiResponse<T> error(
            HttpStatus httpStatus,
            String message,
            T data
    ) {
        return new ApiResponse<>(
                false,
                httpStatus.value(),
                message,
                data
        );
    }

    public static ApiResponse<Void> error(
            HttpStatus httpStatus,
            String message
    ) {
        return error(httpStatus, message, null);
    }
}
