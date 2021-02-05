package com.wini.leader_board_integration.data.dto.leaderboard;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
public class ScoreProfileDto {
    private ScoreDto scoreDto;
    private Map<String,Object> publicData;
    private String reqId;
}
