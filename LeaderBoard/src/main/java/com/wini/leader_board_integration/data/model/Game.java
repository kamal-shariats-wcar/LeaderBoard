package com.wini.leader_board_integration.data.model;

import com.wini.leader_board_integration.data.dto.GameDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Created by kamal on 1/1/2019.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "game")
public class Game extends BaseDoc {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String name;
    private Map<String, Object> configs;
    private Map<String, Object> serverConfigs;

    public Game(GameDto gameDto) {
        this.name = gameDto.getName();
        this.configs = gameDto.getConfigs();
        this.serverConfigs = gameDto.getServerConfigs();
    }
}
