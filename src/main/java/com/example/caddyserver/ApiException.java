package com.example.caddyserver;

/**
 * @author yugasun
 * @date 2024/1/5
 **/
public class ApiException extends RuntimeException {
    public final ApiErrorResponse error;

    public ApiException(ApiError error) {
        super(error.toString());
        this.error = new ApiErrorResponse(error, null, "");
    }

    public ApiException(ApiError error, String data) {
        super(error.toString());
        this.error = new ApiErrorResponse(error, data, "");
    }

    public ApiException(ApiError error, String data, String message) {
        super(message);
        this.error = new ApiErrorResponse(error, data, message);
    }
}
