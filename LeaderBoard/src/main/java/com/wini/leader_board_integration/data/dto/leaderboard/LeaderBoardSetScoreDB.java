package com.wini.leader_board_integration.data.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderBoardSetScoreDB {
    private String leaderBoardId;
    private String playerId;
    private List<Integer> score;
    private Integer finalScore;
    private String country;
}
