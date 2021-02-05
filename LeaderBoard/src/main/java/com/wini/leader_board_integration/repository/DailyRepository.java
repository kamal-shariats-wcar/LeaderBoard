package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.leaderboard.Daily;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DailyRepository extends MongoRepository<Daily, String> {

    Daily findDistinctTopByLeaderboardIdAndPlayerIdOrderByCreatedAtDescExpireAtDesc(String leaderboardId, String playerId);
}
