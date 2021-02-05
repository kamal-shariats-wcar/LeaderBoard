package com.wini.leader_board_integration.service;


import com.wini.leader_board_integration.data.model.GamePlay;

import java.util.List;

/**
 * Created by kamal on 1/5/2019.
 */
public interface GamePlayService {

    GamePlay save(GamePlay gamePlay);

    GamePlay findOne(String gamePlayId);

    List<GamePlay> findAll();

    void delete(String id);
}
