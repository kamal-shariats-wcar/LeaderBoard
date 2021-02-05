package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.model.Profile;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by kamal on 1/5/2019.
 */
public interface ProfileService {
    @CachePut(value = "profile", key = "#profile.id")
    Profile save(Profile profile);

    @Cacheable(value = "profile")
    Profile findOne(String id);

    Object getPublicData(ObjectId profileId);

    Map<String, Object> getPublicDataByUser(String requestedProfileId, String userProfileId);

    Profile findByLoginPlatformPlayerId(String id);

    Profile insertByKey(String path, Object value, String profileId);

    List<Profile> getAll();

    Profile findByToken(String token);

    Profile findByFacebookId(String facebookId);

    Profile findByPhoneNum(String phoneNum);

    Profile findByUserDeletionId(String id);

    Optional<Profile> findById(String profileId);
}
