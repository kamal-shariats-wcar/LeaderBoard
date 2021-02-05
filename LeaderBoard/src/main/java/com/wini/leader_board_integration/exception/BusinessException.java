package com.wini.leader_board_integration.exception;


import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;

import java.util.function.Supplier;

import static com.wini.leader_board_integration.data.enums.ErrorCodeEnum.NOT_FOUND;


public class BusinessException extends RuntimeException implements Supplier<BusinessException> {


    private Integer code;
    private String message;

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message, Integer code, String message1) {
        super(message);
        this.code = code;
        this.message = message1;
    }

    public BusinessException(String message, Throwable cause, Integer code, String message1) {
        super(message, cause);
        this.code = code;
        this.message = message1;
    }

    public BusinessException(Throwable cause, Integer code, String message) {
        super(cause);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Integer code, String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.message = message1;
    }

    public static BusinessException wrap(ErrorCodeEnum errorCodeEnum, String... extraMsg) {
        String msg = errorCodeEnum.getDesc();
        if (extraMsg.length > 0)
            msg = extraMsg[0];
        return new BusinessException(errorCodeEnum.getValue(), msg);
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public BusinessException get() {
        return null;
    }

    public static <T> void throwIfNull(T t, String... msg) {
        if (t == null)
            throw BusinessException.wrap(NOT_FOUND, msg);
    }

}
