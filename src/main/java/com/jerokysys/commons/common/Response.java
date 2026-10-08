package com.jerokysys.commons.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Response<T> {
    private T data;
    private String message;
    private int status;
    private boolean success;

    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .data(data)
                .message("Success")
                .status(200)
                .success(true)
                .build();
    }

    public static <T> Response<T> success(T data, String message) {
        return Response.<T>builder()
                .data(data)
                .message(message)
                .status(200)
                .success(true)
                .build();
    }

    public static <T> Response<T> created(T data, String message) {
        return Response.<T>builder()
                .data(data)
                .message(message)
                .status(201)
                .success(true)
                .build();
    }

    public static Response<Void> success(String message) {
        return Response.<Void>builder()
                .data(null)
                .message(message)
                .status(200)
                .success(true)
                .build();
    }

    public static <T> Response<T> error(String message, int status) {
        return Response.<T>builder()
                .data(null)
                .message(message)
                .status(status)
                .success(false)
                .build();
    }
}
