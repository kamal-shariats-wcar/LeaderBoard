package com.wini.leader_board_integration.data.model;

import com.wini.leader_board_integration.data.dto.GameCategoryDto;
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
@Document(collection = "gameCategory")
public class GameCategory extends BaseDoc {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String name;
    private String title;
    private String imageUrl;
    private Map<String, Object> configs;
    private Map<String, Object> requirementFields;
    private String state;
    private Integer order;
    private String gamePlatformLinkId;
//    @JsonIgnore
//    private List<String> gameModes;

    public GameCategory(GameCategoryDto gameCategoryDto) {
        if (gameCategoryDto.getId() != null) {
            this.id = gameCategoryDto.getId();
        }
        if (gameCategoryDto.getName() != null) {
            this.name = gameCategoryDto.getName();
        }
        if (gameCategoryDto.getTitle() != null) {
            this.title = gameCategoryDto.getTitle();
        }
        if (gameCategoryDto.getImageUrl() != null) {
            this.imageUrl = gameCategoryDto.getImageUrl();
        }
        if (gameCategoryDto.getConfigs() != null) {
            this.configs = gameCategoryDto.getConfigs();
        }
        if (gameCategoryDto.getRequirementFields() != null) {
            this.requirementFields = gameCategoryDto.getRequirementFields();
        }
        if (gameCategoryDto.getState() != null) {
            this.state = gameCategoryDto.getState();
        }
        if (gameCategoryDto.getOrder() != null) {
            this.order = gameCategoryDto.getOrder();
        }
        if (gameCategoryDto.getGamePlatformLinkId() != null) {
            this.gamePlatformLinkId =gameCategoryDto.getGamePlatformLinkId();
        }

    }

}
