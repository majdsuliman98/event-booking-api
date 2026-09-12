package com.majd.Event_booking.common.error;

import java.time.Instant;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log =
        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            NotFoundException exception,
            HttpServletRequest request
    ) {
        log.warn(
            "Resource not found: path={}, message={}",
            request.getRequestURI(),
            exception.getMessage()
    );

        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(
            ConflictException exception,
            HttpServletRequest request
    ) {

        log.warn(
            "Conflict: path={}, message={}",
            request.getRequestURI(),
            exception.getMessage()
        );
        return buildResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));


        log.warn(
            "Validation failed: path={}, message={}",
            request.getRequestURI(),
            message
        );
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message,
            String path
    ) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        return ResponseEntity.status(status).body(error);
    }
}