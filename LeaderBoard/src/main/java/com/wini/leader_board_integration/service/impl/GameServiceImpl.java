package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.model.Game;
import com.wini.leader_board_integration.repository.GameRepository;
import com.wini.leader_board_integration.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kamal on 1/5/2019.
 */
@Service
public class GameServiceImpl implements GameService {
    private static final Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.service.impl.GameServiceImpl.class);
    @Autowired
    GameRepository gameRepository;

    @Override
    public Game save(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public Game findOne(String gameId) {
        return gameRepository.findById(gameId).orElse(null);
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
    @Override
    public Integer getAdsRewardCoin(String gameId) {
        Game game = findOne(gameId);
        if (Objects.nonNull(game)) {
            Map<String, Object> configs = game.getConfigs();
            try {
                Integer coin = (Integer) configs.get("adsCoinReward");
                return coin;
            } catch (Exception ex) {
                logger.warn("error in get adsReward game id: {}", gameId);
                return 0;
            }
        }
        return 0;
    }

    @Override
    public Integer getLoginRewardCoin(String gameId, Integer loginPlatformType) {
        Game game = findOne(gameId);
        if (Objects.nonNull(game)) {
            Map<String, Object> configs = game.getConfigs();
            try {
                Map<String, Object> loginPlatformRewards = (Map<String, Object>) ((Map<String, Object>) configs.get("loginPlatformRewards")).get(loginPlatformType.toString());

                if (loginPlatformRewards != null) {
                    Integer coin = (Integer) loginPlatformRewards.get("rewardAmount");
                    return coin;
                } else {
                    return 0;
                }

            } catch (Exception ex) {
                logger.warn("error in get adsReward game id: {}", gameId);
                return 0;
            }
        }
        return 0;
    }

    @Override
    public Integer getInviteFriendReward(String gameId) {
        Game game = findOne(gameId);
        if (Objects.nonNull(game)) {
            Map<String, Object> configs = game.getConfigs();
            try {
                Integer coin = (Integer) configs.get("inviteCoinReward");
                return coin;
            } catch (Exception ex) {
                logger.warn("error in get inviteCoinReward game id: {}", gameId);
                return 0;
            }
        }
        return 0;
    }


    @Override
    public void delete(String id) {
        gameRepository.deleteById(id);
    }
}
