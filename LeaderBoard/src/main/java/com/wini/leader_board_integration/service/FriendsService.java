package com.wini.leader_board_integration.service;

import com.wini.leader_board_integration.data.model.Friends;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface FriendsService {

    @CachePut(value = "friends",key = "#friends.id")
    @CacheEvict(value = "friendsByProfileId",key = "#friends.profileId")
    Friends save(Friends friends);

    @Cacheable(value = "friends")
    Friends findOne(String id);

    Friends findByProfileId(String profileId);
}
