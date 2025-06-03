package com.uala.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> failure(ApiError error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.error = error;
        return response;
    }
}

