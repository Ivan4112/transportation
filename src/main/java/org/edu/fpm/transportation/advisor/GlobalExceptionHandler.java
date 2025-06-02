package org.edu.fpm.transportation.advisor;

import org.edu.fpm.transportation.exception.AccessDeniedException;
import org.edu.fpm.transportation.exception.ApplicationException;
import org.edu.fpm.transportation.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, String>> handleApplicationException(ApplicationException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getStatusCode()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Resource not found");
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Access denied");
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Bad request");
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParamException(MissingServletRequestParameterException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Missing required parameter");
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Internal server error");
        errorResponse.put("error", e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<Map<String, Object>> handleEnumConversionException(HttpMessageConversionException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Invalid enum value");
        errorResponse.put("error", e.getMessage());
        errorResponse.put("errorCode", "INVALID_ENUM");
        errorResponse.put("status", 400);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> validationErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", "Validation failed");
        errorResponse.put("errors", validationErrors);
        errorResponse.put("status", 400);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
