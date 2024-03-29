package com.example.caddyserver.exception;

/**
 * @author yugasun
 * @date 2024/1/5
 **/
public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected String code;
    protected String message;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BizException(BaseErrorInterface error) {
        super(error.getMessage());
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public BizException(BaseErrorInterface error, Throwable cause) {
        super(error.getMessage(), cause);
        this.code = error.getCode();
        this.message = error.getMessage();
    }

    public BizException(String message) {
        super(message);
        this.message = message;
    }

    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
