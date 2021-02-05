package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.SequenceId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by kamal on 1/14/2019.
 */
public interface SequenceRepository extends MongoRepository<SequenceId,String> {

}
