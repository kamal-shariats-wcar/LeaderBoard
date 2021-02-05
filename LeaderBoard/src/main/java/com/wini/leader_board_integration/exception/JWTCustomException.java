package com.wini.leader_board_integration.exception;


import com.wini.leader_board_integration.data.JWTExceptionType;

public class JWTCustomException extends RuntimeException {
    private JWTExceptionType type;
    private String message;

    public JWTCustomException() {
    }

    public JWTCustomException(JWTExceptionType type, String message) {
        this.type = type;
        this.message = message;
    }

    public JWTCustomException(String message, JWTExceptionType type, String message1) {
        super(message);
        this.type = type;
        this.message = message1;
    }

    public JWTCustomException(String message, Throwable cause, JWTExceptionType type, String message1) {
        super(message, cause);
        this.type = type;
        this.message = message1;
    }

    public JWTCustomException(Throwable cause, JWTExceptionType type, String message) {
        super(cause);
        this.type = type;
        this.message = message;
    }

    public JWTCustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, JWTExceptionType type, String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.type = type;
        this.message = message1;
    }

    public JWTExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
