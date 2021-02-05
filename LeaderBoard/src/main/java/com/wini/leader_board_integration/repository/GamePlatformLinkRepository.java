package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.GamePlatformLink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by kamal on 1/1/2019.
 */
public interface GamePlatformLinkRepository extends MongoRepository<GamePlatformLink,String> {
    @Query("{ 'game.id' : ?0 }")
    List<GamePlatformLink> findAllByGame_Id(String gameId);
    @Query("{ 'platform.id' : ?0 }")
    List<GamePlatformLink> findAllByPlatform_Id(String platformId);
}
