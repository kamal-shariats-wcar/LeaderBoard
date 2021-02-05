package com.wini.leader_board_integration.service;


import com.wini.leader_board_integration.data.model.Game;

import java.util.List;

/**
 * Created by kamal on 1/5/2019.
 */
public interface GameService {

    Game save(Game game);

    Game findOne(String gameId);

    List<Game> getAllGames();

    Integer getAdsRewardCoin(String gameId);

    Integer getLoginRewardCoin(String gameId , Integer loginPlatformType);

    Integer getInviteFriendReward(String game);

    void delete(String id);
}
