package com.wini.leader_board_integration.data;

public enum JWTExceptionType {
    EXPIRED(81,"JWT token expired before."),
    UNSUPPORTED(82,"JWT token isn't supported."),
    MALFORMED(83,"malformed!"),
    SIGNATURE(84,"JWT signature does not match locally computed signature."),
    ILLEGAL_ARGUMENT(85,"Illegal argument.");

    private int code;
    private String msg;

    JWTExceptionType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
