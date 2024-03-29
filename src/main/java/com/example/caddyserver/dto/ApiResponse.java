package com.example.caddyserver.dto;

import com.example.caddyserver.exception.BaseErrorInterface;
import com.example.caddyserver.exception.ExceptionEnum;

/**
 * @author yugasun
 * @date 2024/3/29
 **/
public class ApiResponse {
    /**
     * Response code
     */
    private String code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private Object data;

    public ApiResponse() {
    }

    public ApiResponse(BaseErrorInterface error) {
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Success response
     */
    public static ApiResponse success() {
        return success(null);
    }

    /**
     * Success response
     */
    public static ApiResponse success(Object data) {
        ApiResponse response = new ApiResponse();
        response.setCode(ExceptionEnum.SUCCESS.getCode());
        response.setMessage(ExceptionEnum.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    /**
     * Error response
     */
    public static ApiResponse error(BaseErrorInterface error) {
        ApiResponse response = new ApiResponse();
        response.setCode(error.getCode());
        response.setMessage(error.getMessage());
        response.setData(null);
        return response;
    }

    /**
     * Error response
     */
    public static ApiResponse error(String code, String message) {
        ApiResponse response = new ApiResponse();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    /**
     * Error response
     */
    public static ApiResponse error(String message) {
        ApiResponse response = new ApiResponse();
        response.setCode(ExceptionEnum.SERVER_BUSY.getCode());
        response.setMessage(message);
        response.setData(null);
        return response;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
