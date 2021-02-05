package com.wini.leader_board_integration.data.dto.leaderboard;

public class LeaderBoardNodeDto {
    private int key;
    private String country;
    private String playerId;

    public LeaderBoardNodeDto() {
    }

    public LeaderBoardNodeDto(int key, String country) {
        this.key = key;
        this.country = country;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
