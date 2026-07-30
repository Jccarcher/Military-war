package com.kala.military.adapters.in.rest;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Translates business and payload errors into {@code 400 Bad Request} responses with the shape
 * {@code {"message": "..."}}, so no business failure ever surfaces as a {@code 500}.
 */
@RestControllerAdvice
public final class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String MESSAGE_KEY = "message";

    @ExceptionHandler(IllegalArgumentException.class)
    @NonNull
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(@NonNull IllegalArgumentException exception) {
        logger.warn("Business validation failed: {}", exception.getMessage());
        return badRequest(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @NonNull
    public ResponseEntity<Map<String, String>> handleValidationException(@NonNull MethodArgumentNotValidException exception) {
        var message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage() == null ? error.getField() : error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));
        logger.warn("Payload validation failed: {}", message);
        return badRequest(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @NonNull
    public ResponseEntity<Map<String, String>> handleUnreadableBody(@NonNull HttpMessageNotReadableException exception) {
        logger.warn("Unreadable request body: {}", exception.getMessage());
        return badRequest("El cuerpo de la solicitud es inválido");
    }

    @NonNull
    private ResponseEntity<Map<String, String>> badRequest(@NonNull String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(MESSAGE_KEY, message));
    }
}
