package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.model.Friends;
import com.wini.leader_board_integration.repository.FriendsRepository;
import com.wini.leader_board_integration.service.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendsServiceImpl implements FriendsService {

    @Autowired
    FriendsRepository friendsRepository;

    @Override
    public Friends save(Friends friends) {
        return friendsRepository.save(friends);
    }

    @Override
    public Friends findOne(String id) {
        return friendsRepository.findById(id).orElse(null);
    }

    @Override
    public Friends findByProfileId(String profileId) {
        return friendsRepository.findFirstByProfileId(profileId);
    }

}
