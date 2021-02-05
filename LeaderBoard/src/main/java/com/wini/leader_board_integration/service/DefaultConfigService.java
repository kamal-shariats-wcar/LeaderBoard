package com.wini.leader_board_integration.service;


import com.wini.leader_board_integration.data.dto.DefaultConfigDto;
import com.wini.leader_board_integration.data.info.PublicInfo;
import com.wini.leader_board_integration.data.model.DefaultConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created by kamal on 1/16/2019.
 */
public interface DefaultConfigService {
    @CachePut(value = "defaultConfig",key = "#defaultConfig.id")
    @CacheEvict(value = "defaultConfigByGameId",key = "#defaultConfig.gameId")
    DefaultConfig save(DefaultConfig defaultConfig);
    @Cacheable(value = "defaultConfig")
    DefaultConfig findOne(String defaultConfigId);
    @Cacheable(value = "defaultConfigByGameId")
    DefaultConfig findByGamePlatformLinkId(String gamePlatformLinkId);
    PublicInfo save(final DefaultConfigDto defaultConfig, final String gamePlatformLinkId);
}
