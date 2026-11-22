package com.walter.toolkit.security_starter.exception;

import com.walter.toolkit.security_starter.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

   //handle expected application exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleException(ApiException exception){
        Map<String ,String> details=Map.of("errorCode",exception.getErrorCode());
        return buildResponse(exception.getStatus(),exception.getMessage(), details);
    }

    //handle validation failure on request dto fields
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException validException){
        Map<String,String> errors = new LinkedHashMap<>();
        for(FieldError fieldError:validException.getBindingResult().getFieldErrors()){
            errors.putIfAbsent(fieldError.getField(),
                    fieldError.getDefaultMessage()!=null ?fieldError.getDefaultMessage() :"invalid value");
        }
        return buildResponse(HttpStatus.BAD_REQUEST,"Request validation failed", errors);
    }
    //handle validation failure on method parameters
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException violationException){
        Map<String,String> errors = new LinkedHashMap<>();
        for(ConstraintViolation<?> violation:violationException.getConstraintViolations()){
            errors.put(violation.getPropertyPath().toString(),
                    violation.getMessage());
        }
        return buildResponse(HttpStatus.BAD_REQUEST,"Request Validation Failed",errors);
    }

    //handles springmvc method validation failure
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodValidation(HandlerMethodValidationException exception){
        return buildResponse(HttpStatus.BAD_REQUEST,"Request parameter validation failed",null);
    }

    //Handles malformed json and unreadable request bodies
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnreadableMessages(HttpMessageNotReadableException exception){
        return buildResponse(HttpStatus.BAD_REQUEST,"Request body missing or contains invalid data",null);
    }

    //Handles for unexcepted http methods
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException exception){
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED,"Http method is not supported for this endpoint",null);
    }

    //handle request for resources that do not exist
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFound(NoResourceFoundException foundException){
        return buildResponse(HttpStatus.NOT_FOUND,"Request endpoint was not found",null);
    }
    //final fallback for unexpected application failures
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpectedException(Exception exception){
        log.error("UnExcepted error while processing request",exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,"An Unexpected server error occurred",null);
    }
    //build standard error response
    private ResponseEntity<ApiResponse<Object>> buildResponse(HttpStatus status,String message,Object data){
        ApiResponse<Object> response = new ApiResponse<>(false,message,data, Instant.now());
        return ResponseEntity.status(status).body(response);
    }
    // Handle @PreAuthorize and role authorization failures
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException exception) {
        return buildResponse(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", null);
    }
    // Pass through Spring ResponseStatusException (e.g., 404, 403, 409 from services)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatusException(ResponseStatusException exception) {
        String message = exception.getReason() != null ? exception.getReason() : "Request failed";
        return buildResponse(exception.getStatusCode(), message, null);
    }
    // Handle login and authentication failures
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException exception) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication failed: invalid credentials or token", null);
    }
    // Build standard error response
    private ResponseEntity<ApiResponse<Object>> buildResponse(HttpStatusCode status, String message, Object data) {
        ApiResponse<Object> response = new ApiResponse<>(false, message, data, Instant.now());
        return ResponseEntity.status(status).body(response);
    }
    // Handle disabled or locked accounts (e.g., pending/deactivated agrovets)
    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<ApiResponse<Object>> handleAccountStatusException(AuthenticationException exception) {
        return buildResponse(HttpStatus.FORBIDDEN, "Account is pending approval or has been deactivated", null);
    }
}
