package com.walter.toolkit.security_starter.exception;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public class ApiException extends RuntimeException{
    private final HttpStatus status;
    private final String errorCode;
    public ApiException(HttpStatus status, String errorCode, String message) {
        super((message==null || message.isBlank())? "An Api error occurred":message);
        this.status = Objects.requireNonNull(status,"Http must not be null");
        if(errorCode==null||errorCode.isBlank()){
            throw new IllegalArgumentException("Error code must not be blank");
        }
        this.errorCode = errorCode;
    }
    public HttpStatus getStatus() {
        return status;
    }
    public String getErrorCode() {
        return errorCode;
    }
}
