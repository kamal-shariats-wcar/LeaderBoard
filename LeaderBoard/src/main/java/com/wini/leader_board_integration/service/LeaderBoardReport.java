package com.wini.leader_board_integration.service;

import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.service.impl.ProfileService;

import com.wini.leader_board_integration.service.impl.leaderboard.LeaderboardHolder;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@RequiredArgsConstructor
public abstract class LeaderBoardReport {
    protected final ProfileService profileService;
    private final LeaderboardHolder leaderboardHolder;
    private final MongoTemplate mongoTemplate;
    protected final NotificationService notificationService;


    public abstract void executeReportOperation(final LeaderBoardConfig leaderBoardConfig);

    public final void persistHistory(final JSONObject leaderBoardReportHistory, final String collectionName) {
        mongoTemplate.save(leaderBoardReportHistory, collectionName);
    }

    public final List<RankData> getLeaderBoardTops(final LeaderBoardConfig leaderBoardConfig) {
        final LeaderboardBaseService leaderboardBaseService = leaderboardHolder.getService(leaderBoardConfig.getEveryHours());
        final JSONObject reportConfigs = new JSONObject(leaderBoardConfig.getReportConfigs());
        final List<RankData> rankData = leaderboardBaseService.top(leaderBoardConfig.getId(), "", reportConfigs.optInt("reportCount", 5));
        return rankData;
    }

}
