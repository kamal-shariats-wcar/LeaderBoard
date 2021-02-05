package com.wini.leader_board_integration.service;

import com.hazelcast.core.HazelcastInstance;

import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.service.impl.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class LeaderboardBaseService<T> {

    @Autowired
    ProfileService profileService;
    @Autowired
    HazelcastInstance hazelcastInstance;
    @Autowired
    private LeaderBoardConfigService boardDBService;


    public abstract T save(T t);

    public abstract T findLatest(String leaderboardId, String playerId);

    public abstract RankData rankFor(String leaderbordId, String playerId, List<String> keys, boolean includeCountry);

    public abstract List<RankData> top(String leaderboardId, String playerCountry, int size);

    public abstract List<RankData> topMe(String leaderboardId, RankData playerRank, List<String> keys, boolean includeCountry, int size);

    public abstract List<RankData> bottomMe(String leaderboardId, RankData playerRank, List<String> keys, boolean includeCountry, int size);

    public abstract int getLeaderboardType();

    @SuppressWarnings("Duplicates")
    public Map<String, Object> returnKeys(String playerId, List<String> keys) {
        Profile profile;
        Map<String, Object> result = new HashMap<>();
        profile = profileService.findOne(playerId);
        if (profile != null) {
            Map<String, Object> publicData = profile.getPublicData();
            keys.forEach(key -> result.put(key, publicData.get(key)));
        }
        return result;
    }

    public Optional<LeaderBoardConfig> getLeaderboard(String leaderboardId) {
        LeaderBoardConfig boardDB = (LeaderBoardConfig) hazelcastInstance.getMap("leaderbord")
                .getOrDefault(leaderboardId, boardDBService.findOne(leaderboardId));
        if (boardDB == null)
            boardDB = boardDBService.findOne(leaderboardId);
        return Optional.ofNullable(boardDB);
    }


}
