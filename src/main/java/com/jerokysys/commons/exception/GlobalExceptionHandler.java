package com.jerokysys.commons.exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.jerokysys.commons.common.Response;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException ex) {
        int status = switch (ex.getErrorCode()) {
            case "ENTITY_NOT_FOUND" -> HttpStatus.NOT_FOUND.value();
            case "CONFLICT" -> HttpStatus.CONFLICT.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
        return respond(ex.getErrorCode(), ex.getMessage(), status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + defaultMessage(f))
                .distinct()
                .collect(Collectors.joining("; "));
        if (message.isEmpty()) {
            message = "Validation failed";
        }
        return respond("VALIDATION_ERROR", message, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value '" + ex.getValue() + "' for '" + ex.getName() + "'";
        if (ex.getRequiredType() != null) {
            message = message + " (expected " + ex.getRequiredType().getSimpleName() + ")";
        }
        return respond("TYPE_MISMATCH", message, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response<Void>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String message = "Required parameter '" + ex.getParameterName() + "' is missing";
        return respond("MISSING_PARAMETER", message, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        String message = "Request body is not readable";
        if (ex.getMostSpecificCause() != null && ex.getMostSpecificCause().getMessage() != null) {
            message = message + ": " + ex.getMostSpecificCause().getMessage();
        }
        return respond("MALFORMED_REQUEST", message, HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = "Method " + ex.getMethod() + " is not supported for this endpoint";
        return respond("METHOD_NOT_ALLOWED", message, HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        String message = "Content type"
                + (ex.getContentType() != null ? " '" + ex.getContentType() + "'" : "")
                + " is not supported";
        return respond("UNSUPPORTED_MEDIA_TYPE", message, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Response<Void>> handleNoResource(NoResourceFoundException ex) {
        String message = "No endpoint " + ex.getHttpMethod() + " " + ex.getResourcePath();
        return respond("NOT_FOUND", message, HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ResponseEntity<Response<Void>> handleNoHandler(
            org.springframework.web.servlet.NoHandlerFoundException ex) {
        String message = "No endpoint " + ex.getHttpMethod() + " " + ex.getRequestURL();
        return respond("NOT_FOUND", message, HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Entity not found";
        return respond("ENTITY_NOT_FOUND", message, HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleDataConflict(
            org.springframework.dao.DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return respond("CONFLICT", "The request conflicts with existing data",
                HttpStatus.CONFLICT.value());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return respond("INTERNAL_ERROR", "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ResponseEntity<Response<Void>> respond(String code, String message, int status) {
        return ResponseEntity.status(status).body(Response.error(code, message, status));
    }

    private String defaultMessage(FieldError f) {
        return f.getDefaultMessage() != null ? f.getDefaultMessage() : "is invalid";
    }
}
