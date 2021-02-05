package com.wini.leader_board_integration.data.model;


import com.wini.leader_board_integration.data.enums.LeaderBoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leaderBoardConfig")
@Builder
public class LeaderBoardConfig implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String title;
    private String name;
    private String calculate;
    private List<String> returnKeys;
    private String profileFieldName;
    private String gamePlatformLinkId;
    private Integer topCount;
    private Integer aroundMeCount;
    private Long startTime;
    private Integer everyHours;
    private LeaderBoardType type;
    private String typeValue;
    private Boolean active;
    private int order;
    private boolean includeCountry;
    private Map<String, Object> configs;
    private Map<String, Object> reportConfigs;

}
