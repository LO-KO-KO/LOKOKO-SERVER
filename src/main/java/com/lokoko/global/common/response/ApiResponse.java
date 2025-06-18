package com.lokoko.global.common.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data
) {
    /**
     * Creates a successful API response with the specified HTTP status, message, and data payload.
     *
     * @param httpStatus the HTTP status to include in the response
     * @param message the message describing the result
     * @param data the data payload to include in the response
     * @return an ApiResponse instance representing a successful operation
     */
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

    /**
     * Creates a successful API response with the specified HTTP status and message, without a data payload.
     *
     * @param httpStatus the HTTP status to include in the response
     * @param message the message describing the result
     * @return an ApiResponse instance representing a successful operation with no data
     */
    public static ApiResponse<Void> success(
            HttpStatus httpStatus,
            String message
    ) {
        return success(httpStatus, message, null);
    }

    /**
     * Creates an error ApiResponse with the specified HTTP status, message, and data payload.
     *
     * @param httpStatus the HTTP status to set in the response
     * @param message the error message to include
     * @param data the data payload to include in the response
     * @return an ApiResponse representing an error with the provided details
     */
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

    /**
     * Creates an error ApiResponse with the specified HTTP status and message, without a data payload.
     *
     * @param httpStatus the HTTP status to set in the response
     * @param message the error message to include in the response
     * @return an ApiResponse instance representing an error, with no data
     */
    public static ApiResponse<Void> error(
            HttpStatus httpStatus,
            String message
    ) {
        return error(httpStatus, message, null);
    }
}
