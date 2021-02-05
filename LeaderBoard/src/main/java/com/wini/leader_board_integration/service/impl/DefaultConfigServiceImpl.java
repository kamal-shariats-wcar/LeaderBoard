package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.dto.DefaultConfigDto;
import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.info.PublicInfo;
import com.wini.leader_board_integration.data.info.domain.Error;
import com.wini.leader_board_integration.data.model.DefaultConfig;
import com.wini.leader_board_integration.repository.DefaultConfigsRepository;
import com.wini.leader_board_integration.service.DefaultConfigService;
import com.wini.leader_board_integration.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by kamal on 1/16/2019.
 */
@Service
public class DefaultConfigServiceImpl implements DefaultConfigService {
    Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.service.impl.DefaultConfigServiceImpl.class);
    private final String collectionName = "defaultConfigs";
    @Autowired
    DefaultConfigsRepository defaultConfigsRepository;

    @Autowired
    GameService gameService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public DefaultConfig save(DefaultConfig defaultConfig) {
        return defaultConfigsRepository.save(defaultConfig);

    }

    @Override
    public DefaultConfig findOne(String defaultConfigId) {
        Optional<DefaultConfig> defaultConfig = defaultConfigsRepository.findById(defaultConfigId);
        if (defaultConfig.isPresent()) {
            return defaultConfig.get();
        }
        logger.warn("defaultConfig by id not found", defaultConfigId);
        return null;
    }

    @Override
    public DefaultConfig findByGamePlatformLinkId(String gamePlatformLinkId) {
      return   defaultConfigsRepository.findByGamePlatformLinkId(gamePlatformLinkId);
    }


    @Override
    public PublicInfo save(final DefaultConfigDto defaultConfigDto, final String gamePlatformLinkId) {
        final PublicInfo publicInfo = new PublicInfo();
        DefaultConfig defaultConfig = new DefaultConfig();
        try {
            defaultConfig.setGamePlatformLinkId(gamePlatformLinkId);
            defaultConfig.setMutableData(defaultConfigDto.getMutableData());
            defaultConfig.setPublicData(defaultConfigDto.getPublicData());
            defaultConfig.setPrivateData(defaultConfigDto.getPrivateData());
            save(defaultConfig);
            publicInfo.getResult().put("defaultConfig",defaultConfig);
        } catch (Exception e) {
            publicInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "Error save defaultConfig"));
            logger.warn("Error save defaultConfig");
        }
        return publicInfo;
    }


}
