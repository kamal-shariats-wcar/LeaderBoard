package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.GameCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by kamal on 1/5/2019.
 */
public interface GameCategoryRepository extends MongoRepository<GameCategory,String> {
    List<GameCategory> findGameCategoriesByGamePlatformLinkId(String gamePlatformLinkId);
}
