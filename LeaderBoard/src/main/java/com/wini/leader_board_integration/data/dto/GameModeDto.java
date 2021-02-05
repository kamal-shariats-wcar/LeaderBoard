package com.wini.leader_board_integration.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wini.leader_board_integration.data.model.GameMode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameModeDto {
    private String id;
    private String gameCategoryId;
    private String name;
    private String title;
    private String imageUrl;
    private Map<String, Object> configs;
    private Map<String, Object> requirementFields;
    private String state;
    private Integer order;
    public GameModeDto(GameMode gameMode) {
        final ObjectMapper mapper = new ObjectMapper();
        if (gameMode.getId() != null) {
            this.id = gameMode.getId();
        }
        if (gameMode.getCategoryId() != null) {
            this.gameCategoryId = gameMode.getCategoryId();
        }
        if (gameMode.getName() != null) {
            this.name = gameMode.getName();
        }
        if (gameMode.getTitle() != null) {
            this.title = gameMode.getTitle();
        }
        if (gameMode.getImageUrl() != null) {
            this.imageUrl = gameMode.getImageUrl();
        }
        if (gameMode.getConfigs() != null && !gameMode.getConfigs().isEmpty()) {
            this.configs = gameMode.getConfigs();
        }
        if (gameMode.getRequirementFields() != null && !gameMode.getRequirementFields().isEmpty()) {
            this.requirementFields = gameMode.getRequirementFields();
        }
        if (gameMode.getState() != null) {
            this.state = gameMode.getState();
        }
        if (gameMode.getOrder() != null) {
            this.order = gameMode.getOrder();
        }

    }
}
