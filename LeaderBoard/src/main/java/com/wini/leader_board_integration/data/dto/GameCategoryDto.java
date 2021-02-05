package com.wini.leader_board_integration.data.dto;

import com.wini.leader_board_integration.data.model.GameCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
public class GameCategoryDto {
    private String id;
    private String name;
    private String title;
    private String imageUrl;
    private Map<String, Object> configs;
    private Map<String, Object> requirementFields;
    private String state;
    private Integer order;
    private String gamePlatformLinkId;
    private List<GameModeDto> gameModes;
    public GameCategoryDto(GameCategory gameCategory){
        if (gameCategory.getId() != null) {
            this.id = gameCategory.getId();
        }
        if (gameCategory.getName() != null) {
            this.name = gameCategory.getName();
        }
        if (gameCategory.getTitle() != null) {
            this.title = gameCategory.getTitle();
        }
        if (gameCategory.getImageUrl() != null) {
            this.imageUrl = gameCategory.getImageUrl();
        }
        if (gameCategory.getConfigs() != null && !gameCategory.getConfigs().isEmpty()) {
            this.configs = gameCategory.getConfigs();
        }
        if (gameCategory.getRequirementFields() != null && !gameCategory.getRequirementFields().isEmpty()) {
            this.requirementFields = gameCategory.getRequirementFields();
        }
        if (gameCategory.getState() != null) {
            this.state = gameCategory.getState();
        }
        if (gameCategory.getOrder() != null) {
            this.order = gameCategory.getOrder();
        }

    }
}
