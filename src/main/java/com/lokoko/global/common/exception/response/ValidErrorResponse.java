package com.lokoko.global.common.exception.response;

public record ValidErrorResponse(
        String errorField,
        String errorMessage,
        Object inputValue
) {
    /**
     * Creates a new {@code ValidErrorResponse} instance with the specified field name, error message, and input value.
     *
     * @param field the name of the field that caused the validation error
     * @param msg the validation error message
     * @param value the value that failed validation
     * @return a new {@code ValidErrorResponse} containing the provided details
     */
    public static ValidErrorResponse of(String field, String msg, Object value) {
        return new ValidErrorResponse(field, msg, value);
    }
}
