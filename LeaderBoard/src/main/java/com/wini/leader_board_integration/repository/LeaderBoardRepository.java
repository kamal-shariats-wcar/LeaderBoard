package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LeaderBoardRepository extends MongoRepository<LeaderBoardConfig, String> {

    List<LeaderBoardConfig> findByGamePlatformLinkId(String gamePlatformLinkId);

    //    @Query("select lb from LeaderBoardConfig lb where id in :ids")
    List<LeaderBoardConfig> findByIdIn(List<String> ids);
}
