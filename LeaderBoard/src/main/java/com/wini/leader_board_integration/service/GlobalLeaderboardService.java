package com.wini.leader_board_integration.service;



import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.model.leaderboard.Global;

import java.util.List;

public interface GlobalLeaderboardService {

    void save(Global global);

    List<RankData> top(String leaderboardId, List<String> keys);

    Global findByPlayerId(String playerId);

}
