package com.wini.leader_board_integration.data.dto.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankData {

    private String id;
    private Map<String,Object> returnKeys;
    private Integer score;
    private Integer rank;
    @JsonIgnore
    private Long lastDateScore;
}
