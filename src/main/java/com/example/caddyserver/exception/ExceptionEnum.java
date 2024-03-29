package com.example.caddyserver.exception;

/**
 * @author yugasun
 * @date 2024/3/29
 **/
public enum ExceptionEnum implements BaseErrorInterface {
    SUCCESS("2000", "Success"),
    PARAMETER_INVALID("4000", "Parameter invalid"),
    SERVER_BUSY("5000", "Server busy"),
    NOT_FOUND("4040", "Not found"),
    BODY_NOT_MATCH("4002", "Body not match"),
    DELETE_DOMAIN_ERROR("4004", "Delete domain error"),
    DOMAIN_EXISTED("4001", "Domain Existed"),
    UPDATE_DOMAIN_ERROR("4005", "Update domain error"),
    DOMAIN_NOT_EXIST("4003", "Domain Not exist");

    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
