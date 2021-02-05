package com.wini.leader_board_integration.data.dto.leaderboard;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetLeaderBoardTopDto {
    private String leaderBoardId;
    private String country;
}
