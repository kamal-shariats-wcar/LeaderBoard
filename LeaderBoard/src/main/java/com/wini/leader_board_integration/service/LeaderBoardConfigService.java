package com.wini.leader_board_integration.service;

import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface LeaderBoardConfigService {

    @CachePut(value = "leaderboardConfig", key = "#leaderBoardConfig.id")
    LeaderBoardConfig save(LeaderBoardConfig leaderBoardConfig);


    @CacheEvict(value = "leaderboardConfig", key = "#leaderboardId")
    void delete(String leaderboardId);

    @Cacheable(value = "leaderboardConfig")
    LeaderBoardConfig findOne(String leaderBoardId);

    List<LeaderBoardConfig> findAll();

    List<LeaderBoardConfig> findByGamePlatformLinkId(String gamePlatformLinkId);

    List<LeaderBoardConfig> findInList(List<String> leaderbordIds);
}
