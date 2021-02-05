package com.wini.leader_board_integration.service;

import com.wini.leader_board_integration.data.model.GameCategory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;

/**
 * Created by kamal on 1/5/2019.
 */
public interface GameCategoryService {
    @CachePut(value = "gameCategory", key = "#gameCategory.id")
    @CacheEvict(value = "gameCategoryByGamePlatformLinkId", key = "#gameCategory.gamePlatformLinkId")
    GameCategory save(GameCategory gameCategory);

    @Cacheable(value = "gameCategory")
    GameCategory findOne(String gameId);

    @CacheEvict(value = "gameCategoryByGamePlatformLinkId")
    List<GameCategory> findByGamePlatformLinkId(String gamePlatformLinkId);

    @CacheEvict(value = "gameCategory", key = "#gameCategory.id")
    @Caching(evict = {
            @CacheEvict("gameCategoryByGamePlatformLinkId"),
            @CacheEvict(value = "gameCategory", key = "#gameCategory.id")
    })
    void delete(GameCategory gameCategory);

}
