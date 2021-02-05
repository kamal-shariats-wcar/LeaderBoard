package com.wini.leader_board_integration.data.dto;

import com.wini.leader_board_integration.data.model.Game;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
public class GameDto {
    private String id;
    private String name;
    private Map<String,Object> configs;
    private Map<String,Object> serverConfigs;

    public GameDto(Game game) {
        if (game.getConfigs() != null) {
            this.configs = game.getConfigs();
        }
        if (game.getServerConfigs() != null) {
            this.serverConfigs = game.getServerConfigs();
        }
        if (game.getName() != null) {
            this.name = game.getName();
        }

    }
}
