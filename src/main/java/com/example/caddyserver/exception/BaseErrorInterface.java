package com.example.caddyserver.exception;

/**
 * @author yugasun
 * @date 2024/3/29
 **/
public interface BaseErrorInterface {
    /**
     * Error code
     * @return error code
     */
    String getCode();

    /**
     * Error message
     * @return error message
     */
    String getMessage();
}
