package com.wini.leader_board_integration.service;

import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.Role;
import com.wini.leader_board_integration.data.model.security.User;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.List;
import java.util.Map;

/**
 * Created by kamal on 1/5/2019.
 */
public interface UserService {

    @Caching(
            evict = {
            @CacheEvict(value = "userByProfileId",key = "#user.profileId"),
            @CacheEvict(value = "userByUsername",key = "#user.username")},
            put = {
            @CachePut(value = "user",key = "#user.userId")}
            )
    User save(User user);
    @Cacheable(value = "user")
    User findOne(String userId);
    @Cacheable(value = "userByProfileId")
    User findByProfileId(String profileId);

    Role getUserRole();
    @Cacheable(value = "userByUsername")
    User findByUsername(String username);

    User getUser(Profile profile, String firstName, LoginPlatformType loginPlatformType, final String ip);

    User getUserPlayer(Profile profile, Map<String, Object> playerRegisterVM, LoginPlatformType loginPlatformType);

    Boolean existUser(String username);

    List<User> findByRole(String role);
}
