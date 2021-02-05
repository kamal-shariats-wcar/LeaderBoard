package com.wini.leader_board_integration.data.dto.leaderboard;

import com.wini.leader_board_integration.data.enums.LeaderBoardType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class LeaderBoardDto {

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
    private Map<String, Object> configs;
}
