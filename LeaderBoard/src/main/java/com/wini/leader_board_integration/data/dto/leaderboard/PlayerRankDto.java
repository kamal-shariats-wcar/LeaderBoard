package com.wini.leader_board_integration.data.dto.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRankDto {
    private String playerId;
    private String country;
    private Integer rank;
    private Long lastSetRequestTimestamp;
    private Long lastModifiedTimestamp;
    private Integer score;
}
