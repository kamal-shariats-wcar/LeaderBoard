package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.model.GamePlay;
import com.wini.leader_board_integration.repository.GamePlayRepository;
import com.wini.leader_board_integration.service.GamePlayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kamal on 1/5/2019.
 */
@Service
public class GamePlayServiceImpl implements GamePlayService {
    private static final Logger logger = LoggerFactory.getLogger(GamePlayServiceImpl.class);
    @Autowired
    GamePlayRepository gamePlayRepository;

    @Override
    public GamePlay save(GamePlay gamePlay) {
        return gamePlayRepository.save(gamePlay);
    }

    @Override
    public GamePlay findOne(String gameId) {
        return gamePlayRepository.findById(gameId).orElse(null);
    }

    @Override
    public List<GamePlay> findAll() {
        return gamePlayRepository.findAll();
    }

    @Override
    public void delete(String id) {
        gamePlayRepository.deleteById(id);
    }
}
