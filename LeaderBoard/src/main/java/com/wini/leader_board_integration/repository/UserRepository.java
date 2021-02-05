package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.security.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by kamal on 1/3/2019.
 */
public interface UserRepository extends MongoRepository<User,String> {
    User findByProfileId(String profileId);

    User findByUsername(String username);

    Boolean existsByUsernameIgnoreCase(String username);

    List<User> findAllByRoles_roleName(String username);
}
