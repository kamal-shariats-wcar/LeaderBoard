package com.wini.leader_board_integration.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Created by kamal on 1/1/2019.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "gamePlatformLink")
public class GamePlatformLink extends BaseDoc {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String name;
    private String game;
    private String platform;
    private String gameServerId;
    private Map<String, Object> configs;
    private String defaultLeaderboard;
    private List<String> leaderboards;
    private List<String> gameCategories;
    private List<String> shopCategories;
}
