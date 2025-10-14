package com.mipa.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class BizException extends RuntimeException{
    private final int code;

    public BizException(HttpStatus code, String message) {
        super(message);
        this.code = code.value();
    }

    public BizException(HttpStatus code, String message, Exception e) {
        super(message);
        this.code = code.value();
        log.error(e.toString());
    }

    public int getCode() {
        return code;
    }

    public static BizException badRequest(String msg){
        return new BizException(HttpStatus.BAD_REQUEST, msg);
    }

    public static BizException badRequest(String msg, Exception e){
        return new BizException(HttpStatus.BAD_REQUEST, msg, e);
    }

    public static BizException internalServerError(String msg){
        return new BizException(HttpStatus.BAD_REQUEST, msg);
    }

    public static BizException internalServerError(String msg, Exception e){
        return new BizException(HttpStatus.BAD_REQUEST, msg, e);
    }
}
