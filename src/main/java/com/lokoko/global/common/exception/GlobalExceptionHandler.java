package com.lokoko.global.common.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.lokoko.global.common.exception.response.JsonErrorResponseDetail;
import com.lokoko.global.common.exception.response.MappingJsonErrorResponse;
import com.lokoko.global.common.exception.response.ParseJsonErrorResponse;
import com.lokoko.global.common.exception.response.ValidErrorResponse;
import com.lokoko.global.common.response.ApiResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private <T> ResponseEntity<ApiResponse<T>> buildError(
            HttpStatus status,
            String message,
            T data
    ) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status, message, data));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        return buildError(e.getStatus(), e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ValidErrorResponse>>> handleValidation(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.INVALID_ARGUMENT;
        List<ValidErrorResponse> errors = e.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> ValidErrorResponse.of(
                        fe.getField(),
                        fe.getDefaultMessage(),
                        fe.getRejectedValue()
                ))
                .toList();

        return buildError(errorCode.getStatus(), errorCode.getMessage(), errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode errorCode = ErrorCode.INVALID_ARGUMENT;

        return buildError(errorCode.getStatus(), e.getMessage(), null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException e) {
        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;

        return buildError(errorCode.getStatus(), e.getMessage(), null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        return buildError(errorCode.getStatus(), e.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<List<JsonErrorResponseDetail>>> handleJsonParseException(
            HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        List<JsonErrorResponseDetail> details = new ArrayList<>();

        if (cause instanceof JsonParseException jpe) {
            JsonLocation loc = jpe.getLocation();
            details.add(new ParseJsonErrorResponse(
                    loc.getLineNr(),
                    loc.getColumnNr(),
                    jpe.getOriginalMessage()
            ));
        } else if (cause instanceof JsonMappingException jme) {
            for (Reference ref : jme.getPath()) {
                details.add(new MappingJsonErrorResponse(
                        ref.getFieldName(),
                        jme.getOriginalMessage()
                ));
            }
        } else {
            details.add(new ParseJsonErrorResponse(
                    null, null, cause.getMessage()
            ));
        }

        return buildError(
                ErrorCode.JSON_PARSE_ERROR.getStatus(),
                ErrorCode.JSON_PARSE_ERROR.getMessage(),
                details
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return buildError(errorCode.getStatus(), e.getMessage(), null);
    }
}
