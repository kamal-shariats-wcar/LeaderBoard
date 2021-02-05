package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.model.GameCategory;
import com.wini.leader_board_integration.repository.GameCategoryRepository;
import com.wini.leader_board_integration.service.GameCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kamal on 1/5/2019.
 */
@Service
public class GameCategoryServiceImpl implements GameCategoryService {
    @Autowired
    GameCategoryRepository gameCategoryRepository;

    @Override
    public GameCategory save(GameCategory gameCategory) {
        return gameCategoryRepository.save(gameCategory);
    }

    @Override
    public GameCategory findOne(String gameCategoryId) {
        return gameCategoryRepository.findById(gameCategoryId).orElse(null);
    }

    @Override
    public List<GameCategory> findByGamePlatformLinkId(String gamePlatformLinkId) {
        return gameCategoryRepository.findGameCategoriesByGamePlatformLinkId(gamePlatformLinkId);
    }

    @Override
    public void delete(GameCategory gameCategory) {
        gameCategoryRepository.delete(gameCategory);
    }
}
