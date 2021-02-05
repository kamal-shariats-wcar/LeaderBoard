package com.wini.leader_board_integration.data.info;

import com.wini.leader_board_integration.data.dto.leaderboard.PlayerEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerEntryInfo extends BaseInfo {
    private PlayerEntry result;
}
