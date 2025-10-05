package com.mipa.common.exception;

import org.springframework.http.HttpStatus;

public class BizException extends RuntimeException{
    private final int code;

    public BizException(HttpStatus code, String message) {
        super(message);
        this.code = code.value();
    }

    public int getCode() {
        return code;
    }
}
