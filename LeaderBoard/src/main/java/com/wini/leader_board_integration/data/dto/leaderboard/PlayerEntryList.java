package com.wini.leader_board_integration.data.dto.leaderboard;

import com.wini.leader_board_integration.data.info.BaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntryList extends BaseInfo {
    private List<PlayerEntry> playerEntries;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerEntry{
        private String id;
        private Map<String,Object> returnKeys;
        private Integer score;
        private Integer rank;
    }
}
