package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by kamal on 1/1/2019.
 */
public interface GameRepository extends MongoRepository<Game,String> {

}
