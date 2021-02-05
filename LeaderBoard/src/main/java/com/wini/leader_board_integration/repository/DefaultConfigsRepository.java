package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.DefaultConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by kamal on 1/16/2019.
 */
public interface DefaultConfigsRepository extends MongoRepository<DefaultConfig, String> {
    DefaultConfig findByGamePlatformLinkId(String gamePlatformLinkId);


}
