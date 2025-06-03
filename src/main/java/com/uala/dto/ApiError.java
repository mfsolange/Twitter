package com.uala.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError {
    private String code;
    private String message;

    public ApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
