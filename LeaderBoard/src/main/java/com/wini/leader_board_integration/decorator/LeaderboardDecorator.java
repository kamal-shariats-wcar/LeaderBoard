package com.wini.leader_board_integration.decorator;



import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.dto.leaderboard.ScoreDto;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;

import java.util.List;

public interface LeaderboardDecorator {

    LeaderBoardConfig save(LeaderBoardConfig leaderBoardConfig);

    void delete(String leaderboardId);

    List<RankData> aroundMe(String leaderboardId, String playerId, boolean includeCountry);

    void setScore(ScoreDto scoreDto);

    List<LeaderBoardConfig> findByGamePlatformLinkId(String token);

    List<RankData> entries(String leaderboardId, String token, Boolean includeCountry);
}
