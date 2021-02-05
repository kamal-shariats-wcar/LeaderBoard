package com.wini.leader_board_integration.data.dto.leaderboard;

import com.wini.leader_board_integration.data.info.BaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntry extends BaseInfo {
    private String id;
    private Map<String,Object> returnKeys;
    private Long lastSetRequestTimestamp;
    private Long lastModifiedTimestamp;
    private Object extraData;
    private Integer score;
    private Integer rank;
}
