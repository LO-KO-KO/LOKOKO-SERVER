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

    /**
     * Constructs a ResponseEntity containing an error ApiResponse with the specified HTTP status, message, and data payload.
     *
     * @param status  the HTTP status to set in the response
     * @param message the error message to include in the response
     * @param data    additional error details or payload to include
     * @return a ResponseEntity wrapping the error ApiResponse
     */
    private <T> ResponseEntity<ApiResponse<T>> buildError(
            HttpStatus status,
            String message,
            T data
    ) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status, message, data));
    }

    /**
     * Handles custom BaseException by returning a standardized error response with the exception's status and message.
     *
     * @param e the BaseException thrown within the application
     * @return a ResponseEntity containing an ApiResponse with the error status and message
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e) {
        return buildError(e.getStatus(), e.getMessage(), null);
    }

    /**
     * Handles validation errors for method arguments and returns a standardized error response.
     *
     * Extracts field validation errors from the exception and returns them as a list of {@code ValidErrorResponse} objects with an invalid argument error code.
     *
     * @return a {@code ResponseEntity} containing an {@code ApiResponse} with validation error details.
     */
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

    /**
     * Handles IllegalArgumentException by returning a standardized error response with an invalid argument error code.
     *
     * @param e the IllegalArgumentException thrown during request processing
     * @return a ResponseEntity containing an ApiResponse with error details and HTTP status for invalid arguments
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode errorCode = ErrorCode.INVALID_ARGUMENT;

        return buildError(errorCode.getStatus(), e.getMessage(), null);
    }

    /**
     * Handles cases where a requested resource is not found and returns a standardized error response.
     *
     * @return a ResponseEntity containing an ApiResponse with a RESOURCE_NOT_FOUND error code and the exception message.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException e) {
        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;

        return buildError(errorCode.getStatus(), e.getMessage(), null);
    }

    /**
     * Handles HTTP request method not supported exceptions and returns a standardized error response.
     *
     * @return a ResponseEntity containing an ApiResponse with a METHOD_NOT_ALLOWED error code and the exception message.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

        return buildError(errorCode.getStatus(), e.getMessage(), null);
    }

    /**
     * Handles JSON parsing and mapping errors during HTTP message deserialization.
     *
     * Returns a standardized error response with details about the JSON parse or mapping error, including line and column information for parse errors or field-specific messages for mapping errors.
     *
     * @param ex the exception thrown when the HTTP message cannot be read due to JSON parsing or mapping issues
     * @return a response entity containing an error ApiResponse with a list of JSON error details
     */
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
        }

        return buildError(
                ErrorCode.JSON_PARSE_ERROR.getStatus(),
                ErrorCode.JSON_PARSE_ERROR.getMessage(),
                details
        );
    }

    /**
     * Handles all uncaught exceptions and returns a standardized internal server error response.
     *
     * @param e the exception that was thrown
     * @return a ResponseEntity containing an ApiResponse with an internal server error code and the exception message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAll(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return buildError(errorCode.getStatus(), e.getMessage(), null);
    }
}
