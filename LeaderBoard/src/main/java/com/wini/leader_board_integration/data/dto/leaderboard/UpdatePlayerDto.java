package com.wini.leader_board_integration.data.dto.leaderboard;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePlayerDto {
    private String playerId;
    private String country;
    private String leaderBoardId;
}
