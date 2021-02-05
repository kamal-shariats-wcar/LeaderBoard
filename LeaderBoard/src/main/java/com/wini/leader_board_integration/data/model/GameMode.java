package com.wini.leader_board_integration.data.model;

import com.wini.leader_board_integration.data.dto.GameModeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Created by kamal on 1/3/2019.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "gameMode")
public class GameMode extends BaseDoc {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String categoryId;
    private String name;
    private String title;
    private String imageUrl;
    private Map<String, Object> configs;
    private Map<String, Object> requirementFields;
    private String state;
    private Integer order;
//    private List<String> gameSubModes = new ArrayList<>();

    public GameMode(GameModeDto gameModeDto) {
        if (gameModeDto.getGameCategoryId() != null) {
            this.id = gameModeDto.getId();
        }
        if (gameModeDto.getGameCategoryId() != null) {
            this.categoryId = gameModeDto.getGameCategoryId();
        }
        if (gameModeDto.getName() != null) {
            this.name = gameModeDto.getName();
        }
        if (gameModeDto.getTitle() != null) {
            this.title = gameModeDto.getTitle();
        }
        if (gameModeDto.getImageUrl() != null) {
            this.imageUrl = gameModeDto.getImageUrl();
        }
        if (gameModeDto.getConfigs() != null) {
            this.configs = gameModeDto.getConfigs();
        }
        if (gameModeDto.getRequirementFields() != null) {
            this.requirementFields = gameModeDto.getRequirementFields();
        }
        if (gameModeDto.getState() != null) {
            this.state = gameModeDto.getState();
        }
        if (gameModeDto.getOrder() != null) {
            this.order = gameModeDto.getOrder();
        }

    }

}
