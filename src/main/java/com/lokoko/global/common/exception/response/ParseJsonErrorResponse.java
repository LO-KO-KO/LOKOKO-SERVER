package com.lokoko.global.common.exception.response;

public record ParseJsonErrorResponse(
        Integer line,
        Integer column,
        String message
) implements JsonErrorResponseDetail {
}
