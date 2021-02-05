package com.wini.leader_board_integration.data.enums;

/**
 * Created by kamal on 1/2/2019.
 */
public enum LoginPlatformType {
    NONE(-1, "Non of them"),
    Guest(0, "Guest Player login"),
    FBInstant(1, "player login from FBinstant"),
    FB(2, "player login from Facebook"),
    Google(3, "player login from Google"),
    Credential(4, "player login with password"),
    Twitter(5, "player login from Twitter"),
    SportMob(6, "player login from SportMob");
    //    GameServiceUser(1,"registered user on server"),
    private Integer value;
    private String desc;

    LoginPlatformType(Integer value, String desc) {
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
