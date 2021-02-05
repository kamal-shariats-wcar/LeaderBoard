package com.wini.leader_board_integration.data.enums;

/**
 * Created by kamal on 1/1/2019.
 */
public enum ErrorCodeEnum {
    ERROR(0,"error"),
    FB_ACCESS_TOKEN_INVALID(1,"your facebook access token is not valid"),
    FB_GET_USER_INFO(2,"facebook getUser info Error"),
    LOGIN_ERROR(3,"login error"),
    NOT_FOUND(5,"not found!"),
    KEY_NOT_FOUND(6,"key not found!"),
    INVALID_JSON(4,"invalid json!"),
    IS_USED_BEFORE(7,"This data is consumed before!"),
    INVALID_PURCHASE_TOKEN(8,"Purchase token is invalid!"),
    INVALID(9,"Invalid data!"),
    GAME_PLATFORM_LINK(10, "Game type not found!"),
    PROFILE_NOF_FOUND(11,"Profile not found!"),
    NOT_MATCH(12,"profile with gamePlatformLink doesn't match"),
    CREATION_ERROR(13,"Error in creation profile!"),
    PLAYER_LINKED(14,"player exist and linked"),
    OUT_OF_SERVICE(-1,"The server is out  of service");


    private Integer value;
    private String desc;

    ErrorCodeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
