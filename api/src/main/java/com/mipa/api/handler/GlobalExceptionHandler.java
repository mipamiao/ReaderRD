package com.mipa.api.handler;

import com.mipa.common.exception.BizException;
import com.mipa.common.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ApiResponse<?> handleBizException(BizException ex) {
        return ApiResponse.empty(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<?> handleSqlError(DataIntegrityViolationException ex) {
        return ApiResponse.empty(400, "数据库约束错误：" + ex.getMostSpecificCause().getMessage());
    }


    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception ex) {
        return ApiResponse.empty(500, ex.getMessage());
    }
}
