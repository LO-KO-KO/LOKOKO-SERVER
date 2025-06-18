package com.lokoko.global.common.exception.response;

public record MappingJsonErrorResponse(
        String field,
        String message
) implements JsonErrorResponseDetail {
}
