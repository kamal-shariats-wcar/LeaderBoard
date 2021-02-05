package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.leaderboard.Weekly;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WeeklyRepository extends MongoRepository<Weekly, String> {

    Weekly findDistinctTopByLeaderboardIdAndPlayerIdOrderByCreatedAtDescExpireAtDesc(String leaderboardId, String playerId);

}
