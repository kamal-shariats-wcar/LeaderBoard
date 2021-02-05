package com.wini.leader_board_integration.repository;


import com.wini.leader_board_integration.data.model.leaderboard.ArchiveLeaderbord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArchiveLeaderboardRepository extends MongoRepository<ArchiveLeaderbord, String> {
}
