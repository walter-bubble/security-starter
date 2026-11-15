package com.walter.toolkit.security_starter.response;

import java.time.Instant;
import java.util.Objects;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timeStamp
) {
    //validates the fields when instance is created
    public ApiResponse{
        if(message ==null||message.isBlank()){
            throw new IllegalArgumentException("Response message must not be blank");
        }
        Objects.requireNonNull(timeStamp,"Response timestamp must not be null");
    }
    //for success with data
    public static <T> ApiResponse <T> success(String message,T data){
        return new ApiResponse<>(
                true,
                message,
                data,
                Instant.now()
        );
    }
    //for success with no data returned
    public static ApiResponse<Void>  success(String message){
        return new ApiResponse<>(
                true,
                message,
                null,
                Instant.now()
        );
    }
    //unsuccess without exposing data
    public static <T> ApiResponse<T> unsuccessful(String message){
        return new ApiResponse<>(
                false,
                message,
                null,
                Instant.now()
        );
    }
}
