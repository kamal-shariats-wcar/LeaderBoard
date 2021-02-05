package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.GamePlay;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by kamal on 1/1/2019.
 */
public interface GamePlayRepository extends MongoRepository<GamePlay, String> {

}
