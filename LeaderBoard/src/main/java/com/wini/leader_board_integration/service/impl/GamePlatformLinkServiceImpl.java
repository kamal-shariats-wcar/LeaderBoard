package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.repository.GamePlatformLinkRepository;
import com.wini.leader_board_integration.service.GamePlatformLinkService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kamal on 1/5/2019.
 */
@Service
public class GamePlatformLinkServiceImpl implements GamePlatformLinkService {

    private final GamePlatformLinkRepository gamePlatformLinkRepository;

    public GamePlatformLinkServiceImpl(GamePlatformLinkRepository gamePlatformLinkRepository) {
        this.gamePlatformLinkRepository = gamePlatformLinkRepository;
    }

    @Override
    public GamePlatformLink save(GamePlatformLink gamePlatformLink) {
        return gamePlatformLinkRepository.save(gamePlatformLink);
    }

    @Override
    public GamePlatformLink findOne(String gamePlatformLinkId) {
        return gamePlatformLinkRepository.findById(gamePlatformLinkId).orElse(null);
    }

    @Override
    public List<GamePlatformLink> findAll() {
        return gamePlatformLinkRepository.findAll();
    }

    @Override
    public List<GamePlatformLink> findAllByGame(String gameId) {
        return gamePlatformLinkRepository.findAllByGame_Id(gameId);
    }

    @Override
    public List<GamePlatformLink> findAllByPlatform(String platformId) {
        return gamePlatformLinkRepository.findAllByPlatform_Id(platformId);
    }
}
