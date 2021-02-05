package com.wini.leader_board_integration.data.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDto {
    private Integer score;
    private Object extraData;
    private String playerId;
    private List<String> leaderboards;
}
