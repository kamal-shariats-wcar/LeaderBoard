package com.wini.leader_board_integration.decorator;


import com.wini.leader_board_integration.data.info.*;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.vm.BotProfileVM;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Created by kamal on 1/19/2019.
 */
public interface ProfileDecorator {
    PublicDataInfo getPublicData(String profileId, String token);

    ProfileInfo getProfile(String profileId);

    ProfileInfo updatePublicData(Object publicData, String profileId);

    ProfileInfo updatePrivateData(Object privateData, String profileId);

    PublicInfo updateMutableData(Map<String, Object> mutableData, String profileId);

    CommonInfo createBot(String name, String countryCode, MultipartFile file);

    List<Profile> getAll();

    PublicDataInfo getBotProfile(BotProfileVM botProfileVM);

    public ProfileInfo updateProfileByKey(String profileId, String path, Object value);

    CommonInfo insertCountry(String countryName, String countrCode, MultipartFile file);

    CommonInfo addFriend(String profileId, String token);

    CommonInfo removeFriend(String profileId, String token);

    Map<String, Object> levelUp(String token);

    FriendsInfo getFriends(String token);

    FriendsInfo getFriendsById(String id);

    CommonInfo updatePrivateDataBlock(String profileId, String token);

    CommonInfo updatePrivateDataUnBlock(String profileId, String token);

    PublicInfo findByPhoneNum(String phoneNum);

    PublicInfo checkPhoneStatus(String phoneNum);

    PublicInfo linkPhoneToProfile(String profileId, String phoneNum);
}
