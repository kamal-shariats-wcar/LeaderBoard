package com.wini.leader_board_integration.data.enums;


import com.wini.leader_board_integration.exception.BusinessException;

import java.util.EnumSet;

public enum PaymentPlatform {

    UNKNOWN(0,"unknown"),
    FACEBOOK(1,"facebook"),
    GOOGLE(2,"google"),
    CAFEBAZAAR(3,"cafeBazaar"),
    FACEBOOK_INSTANCE(4,"fbInstance"),
    PAYPAL(5,"paypal"),
    APPSTORE(6,"appStore");



    private int code;
    private String value;



    PaymentPlatform(int code, String value) {
        this.value = value;
        this.code = code;
    }


    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static com.wini.leader_board_integration.data.enums.PaymentPlatform getByCode(int code) {
        return EnumSet.allOf(com.wini.leader_board_integration.data.enums.PaymentPlatform.class)
                .stream()
                .filter(paymentPlatform -> paymentPlatform.getCode() == code)
                .findFirst()
                .orElseThrow(()-> BusinessException.wrap(ErrorCodeEnum.NOT_FOUND));
    }
}
