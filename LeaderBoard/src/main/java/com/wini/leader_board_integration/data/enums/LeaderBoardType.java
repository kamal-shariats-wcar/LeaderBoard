package com.wini.leader_board_integration.data.enums;

public enum LeaderBoardType {

    NONE("none"),
    TOP("top"),
    MASTER("master"),
    GAME_CATEGORY("gameCategory"),
    GAME_MODE("gameMode"),
    GAME_SUB_MODE("gameSubMode");

    private String value;

    LeaderBoardType(String value) {
        this.value = value;
    }
}
