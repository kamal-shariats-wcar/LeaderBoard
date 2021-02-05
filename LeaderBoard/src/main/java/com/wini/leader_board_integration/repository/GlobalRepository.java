package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.leaderboard.Global;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GlobalRepository extends MongoRepository<Global, String> {

    Global findByPlayerId(String playerId);
}
