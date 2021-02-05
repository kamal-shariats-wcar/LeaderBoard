package com.wini.leader_board_integration.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class GamePlatformLinkDto {

    private String id;
    private String name;
    private String gameId;
    private String platformId;
    private Map<String, Object> configs;
    private String leaderBoardId;

    public GamePlatformLinkDto(GamePlatformLink gamePlatformLink) {
        this.id = gamePlatformLink.getId();
        this.name = gamePlatformLink.getName();
        this.configs = gamePlatformLink.getConfigs();
        this.leaderBoardId = gamePlatformLink.getDefaultLeaderboard();
        this.gameId = gamePlatformLink.getGame();
//        this.name = gamePlatformLink.getName();
        this.platformId = gamePlatformLink.getPlatform();

    }
}
