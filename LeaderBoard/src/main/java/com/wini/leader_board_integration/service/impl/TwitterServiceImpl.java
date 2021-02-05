package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.dto.LoginPlatformDataDto;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.model.*;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.domain.LoginData;
import com.wini.leader_board_integration.data.vm.domain.LoginPlatformProfile;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.util.WiniUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("Duplicates")
@Service
public class TwitterServiceImpl implements TwitterService {
    private final UserService userService;
    private final DefaultConfigService defaultConfigService;
    private final ProfileService profileService;
    private final CountryService countryService;
    private final SequenceService sequenceService;
    private final GameService gameService;
    private final String supportUser;
    private final GamePlatformLinkService gamePlatformLinkService;
    public TwitterServiceImpl(UserService userService, DefaultConfigService defaultConfigService,
                              ProfileService profileService, CountryService countryService,
                              SequenceService sequenceService, GameService gameService,
                              @Value("${support-user}") String supportUser, GamePlatformLinkService gamePlatformLinkService) {
        this.userService = userService;
        this.defaultConfigService = defaultConfigService;
        this.profileService = profileService;
        this.countryService = countryService;
        this.sequenceService = sequenceService;
        this.gameService = gameService;
        this.supportUser = supportUser;
        this.gamePlatformLinkService = gamePlatformLinkService;
    }

    @Override
    public Profile createProfile(String ip, LoginData loginData, GamePlatformLink gamePlatformLink) {
        Map<String, Object> publicData;
        TwitterProfile result = verify(loginData, gamePlatformLink.getId());
        final LoginPlatformProfile loginPlatformProfile = new LoginPlatformProfile(String.valueOf(result.getId())+"_"+gamePlatformLink.getGame(),
                result.getName(),
                result.getProfileImageUrl().replace("_normal",""));
        Profile profile = profileService.findByLoginPlatformPlayerId(loginPlatformProfile.getId());
        if (profile == null) {
            DefaultConfig defaultConfig = defaultConfigService.findByGamePlatformLinkId(gamePlatformLink.getId());
            publicData = defaultConfig.getPublicData();
            profile = new Profile();
            try {

                Country country = countryService.findByIp(InetAddress.getByName(ip));
                publicData.put("country", country.getCountryCode());
                publicData.put("countryImageUrl", country.getImageUrl());

            } catch (UnknownHostException e) {
            }

            Map<String, Object> freeCoinInterVal = gamePlatformLink.getConfigs();
            defaultConfig.getPrivateData().put("freeJackpotEndTime", WiniUtil.toEpochSecond() + Long.valueOf(freeCoinInterVal.get("freeJackpotInterval").toString()) * 60);

            defaultConfig.getPrivateData().put("freeSpinnerEndTime", WiniUtil.toEpochSecond() + Long.valueOf(freeCoinInterVal.get("freeSpinnerInterval").toString()) * 60);

            defaultConfig.getPrivateData().put("supportId", ObjectId.get().toString());

            String firstName = result.getName().split("\\s+")[0];
            profile.setGamePlatformLinkId(gamePlatformLink.getId());
//            profile.setLastSendDate(new Date());
            profile.setLoginPlatformPlayerId(loginPlatformProfile.getId());
            profile.setPrivateData(defaultConfig.getPrivateData());
            profile.setMutableData(defaultConfig.getMutableData());

            profile.setPublicData(publicData);
            profile = profileService.save(profile);
            User user = userService.getUser(profile, firstName, LoginPlatformType.Twitter,ip);
            profile.setUserId(user.getUserId());

            Game game = gameService.findOne(gamePlatformLink.getGame());
            String defaultMessage = game.getConfigs().get("SUPPORT_DEFAULT_MESSAGE").toString().replace("{}", firstName);

        }
        profile.getPublicData().replace("name", loginPlatformProfile.getName());
        profile.getPublicData().replace("photoUrl", loginPlatformProfile.getPictureUrl());

        profile.setLoginPlatformData(new LoginPlatformDataDto(loginPlatformProfile, new ArrayList<>()));
        profile = profileService.save(profile);
        return profile;
    }

    @Override
    public User linkGuest(final Profile profile,final  LoginData loginData,final GamePlatformLink gamePlatformLink) {
        final TwitterProfile twitterProfile= verify(loginData,profile.getGamePlatformLinkId());
        final User user = userService.findOne(profile.getUserId());
        final LoginPlatformProfile loginPlatformProfile = new LoginPlatformProfile(String.valueOf(twitterProfile.getId())+"_"+gamePlatformLink.getGame(),
                twitterProfile.getName(),
                twitterProfile.getProfileImageUrl());
        String firstName = twitterProfile.getName().split("\\s+")[0];
        user.setUsername(firstName + "_" + sequenceService.generateSequence(User.SEQUENCE_NAME));
        user.setAuthType(LoginPlatformType.Twitter.getValue());
        user.setFirstName(firstName);
        userService.save(user);
        profile.setLoginPlatformData(new LoginPlatformDataDto(loginPlatformProfile, new ArrayList<>()));
        profile.setLoginPlatformPlayerId(loginPlatformProfile.getId());
        profile.getPublicData().put("name", loginPlatformProfile.getName());
        profile.getPublicData().put("photoUrl", loginPlatformProfile.getPictureUrl());
        profileService.save(profile);
        return user;
    }

    private TwitterProfile verify(LoginData loginData, String gamePlatformLinkId) {
        Map<String, Object> loginKeys = (Map<String, Object>) WiniUtil.loginKeys.get(gamePlatformLinkId);
        Map<String, Map<String, String>> twitterTokens = (Map<String, Map<String, String>>) loginKeys.get(LoginPlatformType.Twitter.getValue().toString());
        final Map<String, String> twitter = twitterTokens.get(loginData.getAuthData().get("clientType"));
        TwitterProfile twitterProfile = new TwitterTemplate(twitter.get("api_key"), twitter.get("api_secret_key"), loginData.getAuthData().get("oauthAccessToken").toString(), loginData.getAuthData().get("oauthTokenSecret").toString())
                .userOperations().getUserProfile();
        return twitterProfile;


    }
}
