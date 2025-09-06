package com.mipa.common.response;

import org.springframework.http.HttpStatus;

// 统一响应结构
public record ApiResponse<T>(int code, String message, T data) {
    public static <T> ApiResponse<T> success(T data ) {
        return new ApiResponse<>(200, "OK", data);
    }
    public static <T>  ApiResponse<T> unauthorized(T data) {
        return new ApiResponse<>(401, "Unauthorized", data);
    }

    public static <T> ApiResponse<T> status(HttpStatus httpStatus, T data) {
        return new ApiResponse<>(httpStatus.value(), httpStatus.getReasonPhrase(), data);
    }

    public static <T> ApiResponse<T> status(HttpStatus httpStatus, String message, T data) {
        return new ApiResponse<>(httpStatus.value(), message, data);
    }
}
