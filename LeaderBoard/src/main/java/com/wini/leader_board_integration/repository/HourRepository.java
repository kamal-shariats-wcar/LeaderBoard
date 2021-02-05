package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.leaderboard.Hour;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface HourRepository extends MongoRepository<Hour, String> {

    Hour findDistinctTopByLeaderboardIdAndPlayerIdOrderByCreatedAtDescExpireAtDesc(String leaderboardId, String playerId);
}
