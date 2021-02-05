package com.wini.leader_board_integration.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.wini.leader_board_integration.data.dto.LoginPlatformDataDto;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.model.*;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.domain.LoginData;
import com.wini.leader_board_integration.data.vm.domain.LoginPlatformProfile;
import com.wini.leader_board_integration.events.NotificationEvent;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.util.WiniUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kamal on 1/1/2019.
 */
@Service
@RequiredArgsConstructor
public class FacebookServiceImpl implements FacebookService {
    private static final Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.service.impl.FacebookServiceImpl.class);


    private final UserService userService;
    private final DefaultConfigService defaultConfigService;
    private final ProfileService profileService;
    private final CountryService countryService;
    private final SequenceService sequenceService;
    private final GameService gameService;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Value("${support-user}")
    private String supportUser;


    @Override
    public JsonNode getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject("https://graph.facebook.com/me/?fields=[\"name\",\"email\",\"picture.width(320)\"]&access_token=" + accessToken.trim(), JsonNode.class);
        return response;

    }

    @Override
    public JsonNode getUserFriends(String accessToken, String userId) {
        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject("https://graph.facebook.com/" + userId + "/friends?fields=[\"id\",\"name\",\"email\",\"picture.width(320)\"]&access_token=" + accessToken.trim(), JsonNode.class);
        return response;
    }

    @Override
    public Profile createProfile(String ip, String accessToken, GamePlatformLink gamePlatformLink) {
        Map<String, Object> publicData;
        JsonNode result = getUserInfo(accessToken);
        final LoginPlatformProfile loginPlatformProfile = new LoginPlatformProfile(result.get("id").asText(), result.get("name").asText(), result.get("picture").get("data").get("url").asText());
        JsonNode friends = getUserFriends(accessToken, loginPlatformProfile.getId()).get("data");
        Profile profile = profileService.findByLoginPlatformPlayerId(loginPlatformProfile.getId());
        List<LoginPlatformProfile> loginPlatformFriendsList = getFBFromData(friends);
        if (profile == null) {
            DefaultConfig defaultConfig = defaultConfigService.findByGamePlatformLinkId(gamePlatformLink.getId());

            Map<String, Object> freeCoinInterVal = gamePlatformLink.getConfigs();
            defaultConfig.getPrivateData().put("freeJackpotEndTime", WiniUtil.toEpochSecond() + Long.valueOf(freeCoinInterVal.get("freeJackpotInterval").toString()) * 60);
            defaultConfig.getPrivateData().put("freeSpinnerEndTime", WiniUtil.toEpochSecond() + Long.valueOf(freeCoinInterVal.get("freeSpinnerInterval").toString()) * 60);

            defaultConfig.getPrivateData().put("supportId", ObjectId.get().toString());

            publicData = defaultConfig.getPublicData();
            profile = new Profile();
            getCountry(ip, publicData);
            String firstName = loginPlatformProfile.getName().split("\\s+")[0];
            profile.setGamePlatformLinkId(gamePlatformLink.getId());
//            profile.setLastSendDate(new Date());
            profile.setLoginPlatformPlayerId(loginPlatformProfile.getId());
            profile.setPrivateData(defaultConfig.getPrivateData());
            profile.setMutableData(defaultConfig.getMutableData());


            profile.setPublicData(publicData);
            profile = profileService.save(profile);
            User user = userService.getUser(profile, firstName, LoginPlatformType.FB,ip);
            profile.setUserId(user.getUserId());

            Game game = gameService.findOne(gamePlatformLink.getGame());
            String defaultMessage = game.getConfigs().get("SUPPORT_DEFAULT_MESSAGE").toString().replace("{}", profile.getPublicData().get("name").toString());
        }
        profile.getPublicData().replace("name", loginPlatformProfile.getName());
        profile.getPublicData().replace("photoUrl", loginPlatformProfile.getPictureUrl());
        profile.setLoginPlatformData(new LoginPlatformDataDto(loginPlatformProfile, loginPlatformFriendsList));


        profile = profileService.save(profile);
        return profile;

    }

    @Override
    public Profile createInstantProfile(final String ip, final LoginData loginData, final GamePlatformLink gamePlatformLink) {
        final Boolean[] firstLogin = {Boolean.FALSE};
        Map<String, Object> publicData;
        Profile profile = profileService.findByLoginPlatformPlayerId(loginData.getLoginPlatformProfile().getId());
        List<LoginPlatformProfile> loginPlatformFriendsList = loginData.getLoginPlatformFriendsList();
        if (profile == null) {
            firstLogin[0] = Boolean.TRUE;
            DefaultConfig defaultConfig = defaultConfigService.findByGamePlatformLinkId(gamePlatformLink.getId());

            Map<String, Object> freeCoinInterVal = gamePlatformLink.getConfigs();
            defaultConfig.getPrivateData().put("freeJackpotEndTime", WiniUtil.toEpochSecond() + Long.valueOf(freeCoinInterVal.get("freeJackpotInterval").toString()) * 60);

            defaultConfig.getPrivateData().put("freeSpinnerEndTime", WiniUtil.toEpochSecond() + Long.valueOf(freeCoinInterVal.get("freeSpinnerInterval").toString()) * 60);

            defaultConfig.getPrivateData().put("supportId", ObjectId.get().toString());

            publicData = defaultConfig.getPublicData();
            profile = new Profile();
            getCountry(ip, publicData);
            String firstName = loginData.getLoginPlatformProfile().getName().split("\\s+")[0];
            profile.setGamePlatformLinkId(gamePlatformLink.getId());
//            profile.setLastSendDate(new Date());
            profile.setLoginPlatformPlayerId(loginData.getLoginPlatformProfile().getId());
            profile.setPublicData(publicData);
            profile.setPrivateData(defaultConfig.getPrivateData());
            profile.setMutableData(defaultConfig.getMutableData());


            profile = profileService.save(profile);
            User user = userService.getUser(profile, firstName, LoginPlatformType.FBInstant,ip);
            profile.setUserId(user.getUserId());

            Game game = gameService.findOne(gamePlatformLink.getGame());
            String defaultMessage = game.getConfigs().get("SUPPORT_DEFAULT_MESSAGE").toString().replace("{}", loginData.getLoginPlatformProfile().getName());
        }
        if (((String) profile.getPublicData().get("country")).equalsIgnoreCase("Unknown")) {
            getCountry(ip, profile.getPublicData());
        }
        profile.getPublicData().replace("name", loginData.getLoginPlatformProfile().getName());
        profile.getPublicData().replace("photoUrl", loginData.getLoginPlatformProfile().getPictureUrl());

        profile.setLoginPlatformData(new LoginPlatformDataDto(loginData.getLoginPlatformProfile(), loginPlatformFriendsList));
        profile = profileService.save(profile);
        if (firstLogin[0]) {
            try {
                sendNotification(profile);
            } catch (Exception e) {
                logger.warn("exception for send notification in create fbInstant profile {}", profile.getId());
                e.printStackTrace();
            }
        }
        return profile;
    }

    private void getCountry(String ip, Map<String, Object> publicData) {
        try {
            Country country = countryService.findByIp(InetAddress.getByName(ip));
            if (country != null) {
                publicData.put("country", country.getCountryCode());
                publicData.put("countryImageUrl", country.getImageUrl());
            }
        } catch (UnknownHostException e) {
            logger.warn("get country Error");
        }
    }

    @Override
    public Profile createFakeInstantProfile(LoginPlatformProfile platformFriend, GamePlatformLink gamePlatformLink, DefaultConfig defaultConfig) {
        Map<String, Object> publicData = defaultConfig.getPublicData();
        Profile profile = new Profile();
        String firstName = platformFriend.getName().split("\\s+")[0];
        profile.setGamePlatformLinkId(gamePlatformLink.getId());
//        profile.setLastSendDate(new Date());
        profile.setLoginPlatformPlayerId(platformFriend.getId());
        profile.setPublicData(publicData);
        profile.setPrivateData(defaultConfig.getPrivateData());
        profile.setMutableData(defaultConfig.getMutableData());
        profile = profileService.save(profile);
        User user = userService.getUser(profile, firstName, LoginPlatformType.FBInstant,"0.0.0.0");
        profile.setUserId(user.getUserId());
        profile.getPublicData().replace("name", platformFriend.getName());
        profile.getPublicData().replace("photoUrl", platformFriend.getPictureUrl());
        profile.setLoginPlatformData(new LoginPlatformDataDto(platformFriend, new ArrayList<>()));
        profile = profileService.save(profile);
        return profile;
    }

    @Override
    public Profile createFakeFBProfile(LoginPlatformProfile platformFriend, GamePlatformLink gamePlatformLink, DefaultConfig defaultConfig) {
        Map<String, Object> publicData = defaultConfig.getPublicData();
        Profile profile = new Profile();
        String firstName = platformFriend.getName().split("\\s+")[0];
        profile.setGamePlatformLinkId(gamePlatformLink.getId());
//        profile.setLastSendDate(new Date());
        profile.setLoginPlatformPlayerId(platformFriend.getId());
        profile.setPublicData(publicData);
        profile.setPrivateData(defaultConfig.getPrivateData());
        profile.setMutableData(defaultConfig.getMutableData());
        profile = profileService.save(profile);
        User user = userService.getUser(profile, firstName, LoginPlatformType.FB,"0.0.0.0");
        profile.setUserId(user.getUserId());
        profile.getPublicData().replace("name", platformFriend.getName());
        profile.getPublicData().replace("photoUrl", platformFriend.getPictureUrl());
        profile.setLoginPlatformData(new LoginPlatformDataDto(platformFriend, new ArrayList<>()));
        profile = profileService.save(profile);
        return profile;
    }

    private List<LoginPlatformProfile> getFromData(JsonNode friends) {
        List<LoginPlatformProfile> loginPlatformProfiles = new ArrayList<>();
        if (friends != null && friends.isArray()) {
            friends.forEach(node -> {
                LoginPlatformProfile loginPlatformProfile = new LoginPlatformProfile(node.get("id").asText(),
                        node.get("name").asText(), node.get("picture").get("parseData").get("url").asText());
                loginPlatformProfiles.add(loginPlatformProfile);
            });
        }
        return loginPlatformProfiles;
    }

    private List<LoginPlatformProfile> getFBFromData(JsonNode friends) {
        List<LoginPlatformProfile> loginPlatformProfiles = new ArrayList<>();
        if (friends != null && friends.isArray()) {
            friends.forEach(node -> {
                LoginPlatformProfile loginPlatformProfile = new LoginPlatformProfile(node.get("id").asText(),
                        node.get("name").asText(), node.get("picture").get("data").get("url").asText());
                loginPlatformProfiles.add(loginPlatformProfile);
            });
        }
        return loginPlatformProfiles;
    }

    public User linkGuest(final Profile profile, final String token) {
        JsonNode result = getUserInfo(token);
        final LoginPlatformProfile loginPlatformProfile = new LoginPlatformProfile(result.get("id").asText(), result.get("name").asText(), result.get("picture").get("data").get("url").asText());
        JsonNode friends = getUserFriends(token, loginPlatformProfile.getId()).get("data");
        List<LoginPlatformProfile> loginPlatformFriendsList = getFromData(friends);
        String firstName = loginPlatformProfile.getName().split("\\s+")[0];
        profile.setLoginPlatformData(new LoginPlatformDataDto(loginPlatformProfile, loginPlatformFriendsList));
        profile.setLoginPlatformPlayerId(loginPlatformProfile.getId());
        profile.getPublicData().replace("name", loginPlatformProfile.getName());
        profile.getPublicData().replace("photoUrl", loginPlatformProfile.getPictureUrl());
        profileService.save(profile);
        User user = userService.findOne(profile.getUserId());
        user.setAuthType(LoginPlatformType.FB.getValue());
        user.setFirstName(firstName);
        user.setUsername(firstName + "_" + sequenceService.generateSequence(User.SEQUENCE_NAME));
        userService.save(user);
        return user;
    }

    @Async
    public void sendNotification(final Profile profile) {
        if (profile.getLoginPlatformData() != null && profile.getLoginPlatformData().getLoginPlatformFriendsList() != null) {
            final Map<String, String> notificationData = new HashMap<>();
            notificationData.put("sender.name", profile.getPublicData().get("name").toString());
            notificationData.put("sender.imageUrl", profile.getPublicData().get("photoUrl").toString());
            profile.getLoginPlatformData().getLoginPlatformFriendsList().forEach(loginPlatformProfile -> {
                final Profile friendProfile = profileService.findByLoginPlatformPlayerId(loginPlatformProfile.getId());
                if (friendProfile != null) {
                    notificationData.put("target.name", friendProfile.getPublicData().get("name").toString());
                    final NotificationEvent notificationEvent = new NotificationEvent(this.getClass(), friendProfile, notificationData, "join");
                    applicationEventPublisher.publishEvent(notificationEvent);
                }
            });
        }
    }
}
