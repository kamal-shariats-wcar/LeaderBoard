package com.wini.leader_board_integration.service;

import com.wini.leader_board_integration.data.model.GamePlatformLink;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created by kamal on 1/5/2019.
 */
public interface GamePlatformLinkService {

    @CachePut(value = "gamePlatformLink", key = "#gamePlatformLink.id")
    GamePlatformLink save(GamePlatformLink gamePlatformLink);

    @Cacheable(value = "gamePlatformLink")
    GamePlatformLink findOne(String gamePlatformLinkId);

    List<GamePlatformLink> findAll();

    List<GamePlatformLink> findAllByGame(String gameId);

    List<GamePlatformLink> findAllByPlatform(String platformId);

}
