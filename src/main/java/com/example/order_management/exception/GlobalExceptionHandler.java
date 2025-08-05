package com.example.order_management.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String paramName = ex.getName();
        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                invalidValue, paramName, expectedType
        );
        
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex, HttpServletRequest request) {
        String message = ex.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
        ResponseStatus statusAnnotation = ex.getClass().getAnnotation(ResponseStatus.class);
        if (statusAnnotation != null) {
            status = statusAnnotation.value();
        } else if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
        }
        
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(body, status);
    }
}
