package com.example.caddyserver.exception;

import com.example.caddyserver.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.validation.FieldError;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yugasun
 * @date 2024/3/29
 **/
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiResponse handleValidationErrors(HttpServletRequest request, MethodArgumentNotValidException e) {
        logger.error("BizException: {}", e.getMessage());
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList());
        return ApiResponse.error(getErrorsMap(errors).toString());

    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }


    /**
     * Handle custom business exception
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = {BizException.class, IllegalArgumentException.class})
    @ResponseBody
    public ApiResponse bizExceptionHandler(HttpServletRequest request, BizException e) {
        logger.error("BizException: {}", e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * Handle null pointer exception
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ApiResponse exceptionHandler(HttpServletRequest request, NullPointerException e) {
        logger.error("NullPointerException: {}", e.getMessage());
        return ApiResponse.error(ExceptionEnum.BODY_NOT_MATCH);
    }

    /**
     * Handle other exceptions
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResponse exceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("Unknown Exception: {}", e);
        return ApiResponse.error(ExceptionEnum.SERVER_BUSY);
    }


}
