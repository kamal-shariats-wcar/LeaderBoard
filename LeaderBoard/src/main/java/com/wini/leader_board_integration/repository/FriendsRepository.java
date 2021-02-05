package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.Friends;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FriendsRepository extends MongoRepository<Friends,String> {
    Friends findFirstByProfileId(String profileId);
}
