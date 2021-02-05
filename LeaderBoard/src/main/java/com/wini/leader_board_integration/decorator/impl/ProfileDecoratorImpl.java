package com.wini.leader_board_integration.decorator.impl;

import com.wini.leader_board_integration.config.security.JwtTokenUtil;
import com.wini.leader_board_integration.data.dto.ProfileDto;
import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.info.*;
import com.wini.leader_board_integration.data.info.domain.Error;
import com.wini.leader_board_integration.data.model.*;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.BotProfileVM;
import com.wini.leader_board_integration.decorator.ProfileDecorator;
import com.wini.leader_board_integration.events.NotificationEvent;
import com.wini.leader_board_integration.exception.BusinessException;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.service.impl.ProfileService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kamal on 1/19/2019.
 */
@Service
@RequiredArgsConstructor
public class ProfileDecoratorImpl implements ProfileDecorator {
    private final Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.decorator.impl.ProfileDecoratorImpl.class);
    private final ProfileService profileService;
    private final FacebookService facebookService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final GamePlatformLinkService gamePlatformLinkService;
    private final GameService gameService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final StorageService storageService;
    private final CountryService countryService;
    private final FriendsService friendsService;
    private final DefaultConfigService defaultConfigService;


    @Override
    public PublicDataInfo getPublicData(final String profileId, final String token) {
        final PublicDataInfo publicDataInfo = new PublicDataInfo();
        final String profileIdRequest = jwtTokenUtil.getProfileIdfromToken(token.substring(7));
        try {
            final Map<String, Object> publicData = profileService.getPublicDataByUser(profileId, profileIdRequest);
            if (publicData == null) {
                publicDataInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "profile not found!"));
            } else {
                publicDataInfo.getResult().put("profileId", profileId);
                publicDataInfo.getResult().put("publicData", publicData);
                logger.debug("getPublicData for profileId: {} is : \n {} ", profileId, publicData.toString());
            }
        } catch (Exception e) {
            publicDataInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "invalid profileId"));
        }
        return publicDataInfo;
    }

    @Override
    public ProfileInfo getProfile(final String profileId) {
        final ProfileInfo profileInfo = new ProfileInfo();
        try {
            final ProfileDto profileDto = new ProfileDto(profileService.findOne(profileId));
            profileInfo.setProfile(profileDto);
        } catch (Exception e) {
            logger.warn("profile not found {}", profileId);
            profileInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "profile not found"));
        }
        return profileInfo;
    }

    @Override
    public ProfileInfo updatePublicData(Object publicData, String profileId) {
        ProfileInfo profileInfo = new ProfileInfo();
        Profile profile = profileService.findOne(profileId);
        if (profile == null) {
            logger.warn("profile not found {}", profileId);
            profileInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "profile not found"));
            return profileInfo;
        }
        Map<String, Object> oldPublicData = profile.getPublicData();
        Map<String, Object> newPublicData = (Map<String, Object>) publicData;
        newPublicData.replace("countryImageUrl", oldPublicData.get("countryImageUrl"));
        profile.setPublicData(newPublicData);
        profileService.save(profile);
//        if (profile.getPublicData().get("countryImageUrl") != null) {
//            profile.getPublicData().replace("countryImageUrl", (countryImagesPath + profile.getPublicData().get("countryImageUrl").toString()));
//        }
        if (profile.getMutableData() != null) {
            profile.getPublicData().put("followMe", profile.getMutableData().get("followMe"));
        }
        profileInfo.setProfile(new ProfileDto(profile));
        return profileInfo;
    }

    @Override
    public CommonInfo updatePrivateDataBlock(String profileId, String token) {
        CommonInfo commonInfo = new CommonInfo();
        Profile profile = profileService.findByToken(token);
        List<String> blockList;
        if (profile.getPrivateData().get("blockList") != null) {
            blockList = (List<String>) profile.getPrivateData().get("blockList");
        } else {
            blockList = new ArrayList<>();
        }
        if (blockList.contains(profileId)) {
            commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "profile exist!"));
            return commonInfo;
        } else {
            blockList.add(profileId);
            profile.getPrivateData().put("blockList", blockList);
        }
        profileService.save(profile);
        commonInfo.setSavedId(profileId);
        return commonInfo;
    }

    @Override
    public CommonInfo updatePrivateDataUnBlock(String profileId, String token) {
        CommonInfo commonInfo = new CommonInfo();
        Profile profile = profileService.findByToken(token);
        List<String> blockList;
        if (profile.getPrivateData().get("blockList") != null) {
            blockList = (List<String>) profile.getPrivateData().get("blockList");
        } else {
            blockList = new ArrayList<>();
        }
        if (blockList.contains(profileId)) {
            blockList.remove(profileId);
            profile.getPrivateData().put("blockList", blockList);
        } else {
            commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "profile does not block!"));
            return commonInfo;
        }
        profileService.save(profile);
        commonInfo.setSavedId(profileId);
        return commonInfo;
    }


    @Override
    public ProfileInfo updatePrivateData(Object privateData, String profileId) {
        ProfileInfo profileInfo = new ProfileInfo();
        Profile profile = profileService.findOne(profileId);
        if (profile == null) {
            logger.warn("profile not found {}", profileId);
            profileInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "profile not found"));
            return profileInfo;
        }
        profile.setPrivateData((Map<String, Object>) privateData);
        profileService.save(profile);
//        if (profile.getPublicData().get("countryImageUrl") != null) {
//            profile.getPublicData().replace("countryImageUrl", (countryImagesPath + profile.getPublicData().get("countryImageUrl").toString()));
//        }
        if (profile.getMutableData() != null) {
            profile.getPublicData().put("followMe", profile.getMutableData().get("followMe"));
        }
        profileInfo.setProfile(new ProfileDto(profile));
        return profileInfo;
    }

    @Override
    public PublicInfo updateMutableData(Map<String, Object> mutableData, String profileId) {
        PublicInfo publicInfo = new PublicInfo();
        final Profile profile = profileService.findOne(profileId);
        if (profile == null) {
            logger.warn("profile not found {}", profileId);
            publicInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "profile not found"));
            return publicInfo;
        }
        profile.setMutableData(mutableData);
        profileService.save(profile);
//        if (profile.getPublicData().get("countryImageUrl") != null) {
//            profile.getPublicData().replace("countryImageUrl", (countryImagesPath + profile.getPublicData().get("countryImageUrl").toString()));
//        }
        if (profile.getMutableData() != null) {
            profile.getPublicData().put("followMe", mutableData.get("followMe"));
        }
        publicInfo.getResult().put("profile", new ProfileDto(profile));
        return publicInfo;
    }

    @Override
    public CommonInfo createBot(String name, String countryCode, MultipartFile file) {
        return null;
    }




    @Override
    public List<Profile> getAll() {
        return profileService.getAll();
    }

    @Override
    public PublicDataInfo getBotProfile(BotProfileVM botProfileVM) {
        return null;
    }


    @Override
    public ProfileInfo updateProfileByKey(String profileId, String path, Object value) {
        ProfileInfo profileInfo = new ProfileInfo();
        Profile profile = profileService.findOne(profileId);
        if (profile == null) {
            profileInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "profile not found"));
            return profileInfo;
        }
        profile = profileService.insertByKey(path, value, profile.getId());
        profileInfo.setProfile(new ProfileDto(profile));
        return profileInfo;
    }

    @Override
    public CommonInfo insertCountry(String countryName, String countrycode, MultipartFile file) {
        CommonInfo commonInfo = new CommonInfo();
        Country country = countryService.findByCountryCode(countrycode);
        if (country != null) {
            commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "country code already exist"));
            return commonInfo;
        }
        final String fileName = storageService.store(file);
        country = new Country(null, countryName, countrycode, fileName, null);
        country = countryService.save(country);
        commonInfo.setSavedId(country.getId());
        return commonInfo;
    }

    @Override
    public CommonInfo addFriend(String friendProfileId, String token) {
        CommonInfo commonInfo = new CommonInfo();
        final String authToken = token.substring(7);
        final String profileId = jwtTokenUtil.getProfileIdfromToken(authToken);
        Friends owner = friendsService.findByProfileId(profileId);
        Profile friend = profileService.findOne(friendProfileId);
        if (friend == null) {
            commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "friend with this profileId not found."));
            logger.warn("friend with this profileId not found. {}", friendProfileId);
            return commonInfo;
        }
        Map<String, Object> mutableData = friend.getMutableData();
        Map<String, Object> privateData = friend.getPrivateData();
        List<String> blockList = (List<String>) privateData.getOrDefault("blockList", new ArrayList<>());
        if (blockList != null) {
            if (blockList.contains(profileId)) {
                commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "friend blocked you some times ago."));
                return commonInfo;
            }
        }
        if (mutableData.containsKey("followMe") && mutableData.get("followMe").equals(true)) {


           /* if (!owner.getGamePlatformLink().getGame().getId().equals(friend.getGamePlatformLink().getGame().getId())) {
                commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "friend gameId with this profile  does not match."));
                logger.warn("friend with this profileId not found. {}", friendProfileId);
                return commonInfo;
            }*/
            if (owner == null) {
                owner = new Friends();
                owner.setProfileId(profileId);
            }
            if (owner.getGameFriendsList() == null) {
                owner.setGameFriendsList(new ArrayList<>());
            }
            if (owner.getGameFriendsList().stream().anyMatch(profile -> profile.getId().equals(friendProfileId))) {
                commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "friend exist."));
                logger.warn("friend exist for . {}", owner.getId());
                return commonInfo;
            }
            owner.getGameFriendsList().add(friend);
            friendsService.save(owner);
            /* send notification */
            final Map<String, String> notificationData = new HashMap<>();
            final Profile ownerProfile = profileService.findOne(profileId);
            notificationData.put("target.name", friend.getPublicData().get("name").toString());
            notificationData.put("sender.name", ownerProfile.getPublicData().get("name").toString());
            notificationData.put("sender.imageUrl", ownerProfile.getPublicData().get("photoUrl").toString());
            final NotificationEvent notificationEvent = new NotificationEvent(this.getClass(), friend, notificationData, "follow");
            applicationEventPublisher.publishEvent(notificationEvent);

        } else {
            commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "you can not follow this friend"));
        }


        commonInfo.setSavedId(friend.getId());
        return commonInfo;
    }

    @Override
    public CommonInfo removeFriend(String friendProfileId, String token) {
        CommonInfo commonInfo = new CommonInfo();
        final String authToken = token.substring(7);
        Friends owner = friendsService.findByProfileId(jwtTokenUtil.getProfileIdfromToken(authToken));
        if (owner == null) {
            commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "friendList is empty for this profileId."));
            logger.warn("friendList is empty for this profileId. {}", friendProfileId);
            return commonInfo;
        }
        boolean finded = owner.getGameFriendsList().removeIf(profile -> profile.getId().equals(friendProfileId));
        if (finded) {
            friendsService.save(owner);
        } else {
            commonInfo.setError(new Error(ErrorCodeEnum.ERROR.getValue(), "friendList does not contain this profileId."));
            logger.warn("friendList does not contain this profileId. {}", friendProfileId);
            return commonInfo;
        }
        commonInfo.setSavedId(friendProfileId);
        return commonInfo;
    }

    @Override
    public Map<String, Object> levelUp(String token) {

        Profile profile = profileService.findByToken(token);
        BusinessException.throwIfNull(profile);

        Map<String, Object> publicData = profile.getPublicData();

        int lastSeenLevelUp = Integer.parseInt(publicData.get("levelupLastSeen").toString());
        int level = Integer.parseInt(publicData.get("level").toString());
        int rewardCount = 0;
        if (lastSeenLevelUp < level) {
            GamePlatformLink gamePlatformLink = gamePlatformLinkService.findOne(profile.getGamePlatformLinkId());
            BusinessException.throwIfNull(gamePlatformLink);

            String gameId = gamePlatformLink.getGame();

            Game game = gameService.findOne(gameId);
            BusinessException.throwIfNull(game);

            Map<String, Object> levelup = (Map<String, Object>) game.getConfigs().get("levelupReward");

            List<Integer> rewards = (List<Integer>) levelup.get("rewards");


            if (lastSeenLevelUp + 1 < rewards.size() && level + 1 < rewards.size())
                rewardCount = rewards.subList(lastSeenLevelUp + 1, level + 1).stream().reduce(0, Integer::sum);
            else {

                if (lastSeenLevelUp + 1 >= rewards.size()) {
                    rewardCount = rewards.get(rewards.size() - 1);
                } else {
                    rewardCount = rewards.subList(lastSeenLevelUp + 1, rewards.size() - 1).stream().reduce(0, Integer::sum);
                }
            }

            int oldReward = Integer.parseInt(publicData.get(levelup.get("rewardType").toString()).toString());
            publicData.put(levelup.get("rewardType").toString(), rewardCount + oldReward);
            publicData.put("levelupLastSeen", level);
            profile.setPublicData(publicData);
            profileService.save(profile);
            try {
                sendNotification(profile);
            } catch (Exception exception) {
                logger.warn("level up send notification error profile id {}", profile.getId());
            }
        }

        return publicData;
    }

    private void sendNotification(final Profile profile) {
        if (profile.getLoginPlatformData() != null && profile.getLoginPlatformData().getLoginPlatformFriendsList() != null) {
            final Map<String, String> notificationData = new HashMap<>();
            notificationData.put("sender.name", profile.getPublicData().get("name").toString());
            notificationData.put("sender.imageUrl", profile.getPublicData().get("photoUrl").toString());
            notificationData.put("sender.level", profile.getPublicData().get("level").toString());
            profile.getLoginPlatformData().getLoginPlatformFriendsList().forEach(loginPlatformProfile -> {
                final Profile friendProfile = profileService.findByLoginPlatformPlayerId(loginPlatformProfile.getId());
                notificationData.put("target.name", friendProfile.getPublicData().get("name").toString());
                if (friendProfile != null) {

                    final NotificationEvent notificationEvent = new NotificationEvent(this.getClass(), friendProfile, notificationData, "levelUp");
                    applicationEventPublisher.publishEvent(notificationEvent);
                }
            });
        }
    }

    @Override
    public FriendsInfo getFriends(String token) {

        Profile profile = profileService.findByToken(token);
        return getFriendsInfo(token, profile);
    }

    @NotNull
    private FriendsInfo getFriendsInfo(String token, Profile profile) {
        FriendsInfo friendsInfo = new FriendsInfo();
        List<HashMap<String, Object>> friends = new ArrayList<>();
        List<HashMap<String, Object>> loginPlatformFriends = new ArrayList<>();
        HashMap<String, HashMap<String, Object>> addedFriends = new HashMap<>();
        GamePlatformLink gamePlatformLink = gamePlatformLinkService.findOne(profile.getGamePlatformLinkId());
        DefaultConfig defaultConfig = defaultConfigService.findByGamePlatformLinkId(profile.getGamePlatformLinkId());

        Friends profileFriends = friendsService.findByProfileId(profile.getId());
        if (profileFriends != null && profileFriends.getGameFriendsList() != null) {
            profileFriends.getGameFriendsList().forEach(friend -> {
                HashMap<String, Object> friendData = new HashMap<>();
                friendData.put("profileId", friend.getId());
                Map<String, Object> publicData = profileService.findById(friend.getId()).map(Profile::getPublicData).orElse(null);
                publicData.put("rank", -1);
                friendData.put("publicData", publicData);
                friends.add(friendData);
                if (profile.getLoginPlatformData() != null && profile.getLoginPlatformData().getLoginPlatformProfile() != null) {
                    addedFriends.put(profile.getLoginPlatformData().getLoginPlatformProfile().getId(), friendData);
                }

            });
        }
        if (profile.getLoginPlatformData() != null && profile.getLoginPlatformData().getLoginPlatformFriendsList() != null) {
            profile.getLoginPlatformData().getLoginPlatformFriendsList().forEach(platformFriend -> {
                HashMap<String, Object> friendData = new HashMap<>();
                if (addedFriends.containsKey(platformFriend.getId())) {
                    loginPlatformFriends.add(addedFriends.get(platformFriend.getId()));
                } else {
                    Profile friend = profileService.findByLoginPlatformPlayerId(platformFriend.getId());
                    if (friend != null) {
                        friendData.put("profileId", friend.getId());
                        friendData.put("loginPlatformPlayerId", friend.getLoginPlatformPlayerId());
                        Map<String, Object> publicData = friend.getPublicData();
                        publicData.put("rank", -1);
                        friendData.put("publicData", publicData);
                        loginPlatformFriends.add(friendData);
                    } else {
                        final User user = userService.findOne(profile.getUserId());
                        if (user.getAuthType().equals(LoginPlatformType.FBInstant.getValue())) {
                            Profile fakeProfile = facebookService.createFakeInstantProfile(platformFriend, gamePlatformLink, defaultConfig);
                            Map<String, Object> fakePublicData = fakeProfile.getPublicData();
                            fakePublicData.put("rank", -1);
                            fakePublicData.put("countryImageUrl", fakePublicData.get("countryImageUrl").toString());
                            friendData.put("loginPlatformPlayerId", platformFriend.getId());
                            friendData.put("profileId", fakeProfile.getId());
                            friendData.put("publicData", fakePublicData);
                        } else if (user.getAuthType().equals(LoginPlatformType.FB.getValue())) {
                            Profile fakeProfile = facebookService.createFakeFBProfile(platformFriend, gamePlatformLink, defaultConfig);
                            Map<String, Object> fakePublicData = fakeProfile.getPublicData();
                            fakePublicData.put("rank", -1);
                            fakePublicData.put("countryImageUrl", fakePublicData.get("countryImageUrl").toString());
                            friendData.put("loginPlatformPlayerId", platformFriend.getId());
                            friendData.put("profileId", fakeProfile.getId());
                            friendData.put("publicData", fakePublicData);
                        }
                    }
                }

            });
        }
        friendsInfo.getResult().put("gameFriends", friends);
        friendsInfo.getResult().put("loginPlatformFriends", loginPlatformFriends);
        return friendsInfo;
    }

    @Override
    public FriendsInfo getFriendsById(String id) {

        Profile profile = profileService.findOne(id);
        return getFriendsInfo(id, profile);
    }


    @Override
    public PublicInfo findByPhoneNum(String phoneNum) {
        Profile profile = profileService.findByPhoneNum(phoneNum);
        PublicInfo info = new PublicInfo();
        if (profile == null)
            return info.addProperty("exist", false);
        else {
            info.addProperty("exist", true);
            info.addProperty("authType", userService.findByProfileId(profile.getId()).getAuthType());
        }
        return info;
    }

    @Override
    public PublicInfo checkPhoneStatus(String phoneNum) {
        Profile profile = profileService.findByPhoneNum(phoneNum);
        PublicInfo info = new PublicInfo();
        if (profile == null)
            return info.addProperty("exist", false);
        else {
            info.addProperty("exist", true);
            Integer authType = userService.findByProfileId(profile.getId()).getAuthType();
            if (authType.equals(LoginPlatformType.Guest.getValue())) {
                info.addProperty("profileId", profile.getId());
            } else
                info.addProperty("authType", authType);
        }
        return info;
    }

    @Override
    public PublicInfo linkPhoneToProfile(String profileId, String phoneNum) {
        PublicInfo info = new PublicInfo();
        if (profileService.findByPhoneNum(phoneNum) != null)
            throw BusinessException.wrap(ErrorCodeEnum.INVALID, "This number has linked before!");

        Profile profile = profileService.findOne(profileId);
        BusinessException.throwIfNull(profile);

        profile.getPrivateData().put("phoneNum", phoneNum);
        profileService.save(profile);

        return info;
    }
}
