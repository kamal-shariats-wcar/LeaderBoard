package com.wini.leader_board_integration.data.model.leaderboard;


import com.wini.leader_board_integration.data.model.leaderboard.enums.LeaderBoardResetTime;
import com.wini.leader_board_integration.data.model.leaderboard.enums.LeaderBoardSubmitScoreType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
public class LeaderBoard implements Serializable{
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private LeaderBoardResetTime reset;
    private Integer every;
    private LeaderBoardSubmitScoreType type;
    private List<String> returnKeys ;
    private Boolean isUpdateProfileField;
    private String profileFieldName;
    private String gameType;
    private String gameTypeId;
    private Long startTime;
    private Long endTime;
    private SortedSet<Node> globalLeaderBoard = Collections.synchronizedSortedSet(new TreeSet<>());
    private Map<String, SortedSet<Node>> countriesLeaderBoard = new ConcurrentHashMap<>();
    private Integer topCount;
    private Integer aroundMeCount;

}

