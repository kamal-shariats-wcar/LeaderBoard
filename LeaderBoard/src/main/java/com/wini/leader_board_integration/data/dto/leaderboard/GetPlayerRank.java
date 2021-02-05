package com.wini.leader_board_integration.data.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPlayerRank {
    private String playerId;
    private String leaderBoardId;
    private Boolean country = false;

}
