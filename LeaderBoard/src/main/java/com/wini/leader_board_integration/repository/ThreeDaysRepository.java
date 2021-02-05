package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.leaderboard.ThreeDays;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThreeDaysRepository extends MongoRepository<ThreeDays, String> {

    ThreeDays findDistinctTopByLeaderboardIdAndPlayerIdOrderByCreatedAtDescExpireAtDesc(String leaderboardId, String playerId);

}
