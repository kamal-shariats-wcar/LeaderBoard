package com.wini.leader_board_integration.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.wini.leader_board_integration.data.dto.LoginPlatformDataDto;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.model.*;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.domain.LoginData;
import com.wini.leader_board_integration.data.vm.domain.LoginPlatformProfile;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.util.WiniUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("Duplicates")
@Service
public class GoogleServiceImpl implements GoogleService {

    private final UserService userService;
    private final DefaultConfigService defaultConfigService;
    private final ProfileService profileService;
    private final CountryService countryService;
    private final SequenceService sequenceService;
    private final GamePlatformLinkService gamePlatformLinkService;
    @Value("${support-user}")
    private String supportUser;

    @Autowired
    private GameService gameService;

    public GoogleServiceImpl(final SequenceService sequenceService, UserService userService, DefaultConfigService defaultConfigService, ProfileService profileService, CountryService countryService, GamePlatformLinkService gamePlatformLinkService) {
        this.userService = userService;
        this.defaultConfigService = defaultConfigService;
        this.profileService = profileService;
        this.countryService = countryService;
        this.sequenceService = sequenceService;
        this.gamePlatformLinkService = gamePlatformLinkService;
    }

    @Override
    public JsonNode getUserInfo(String accessToken) {
        return null;
    }

    @Override
    public Object getUserFriends(String accessToken, String userId) {
        return null;
    }

    @Override
    public Profile createProfile(String ip, LoginData loginData, GamePlatformLink gamePlatformLink) {
        Map<String, Object> publicData;
        Map<String, Object> result = verify(loginData, gamePlatformLink);
        final LoginPlatformProfile loginPlatformProfile = new LoginPlatformProfile(result.get("id").toString(),
                result.get("name").toString(),
                result.get("pictureUrl").toString());
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

            String firstName = result.get("givenName").toString();
            profile.setGamePlatformLinkId(gamePlatformLink.getId());
//            profile.setLastSendDate(new Date());
            profile.setLoginPlatformPlayerId(loginPlatformProfile.getId());
            profile.setPrivateData(defaultConfig.getPrivateData());
            profile.setMutableData(defaultConfig.getMutableData());

            profile.setPublicData(publicData);
            profile = profileService.save(profile);
            User user = userService.getUser(profile, firstName, LoginPlatformType.Google,ip);
            profile.setUserId(user.getUserId());

            Game game = gameService.findOne(gamePlatformLink.getGame());
            String defaultMessage = game.getConfigs().get("SUPPORT_DEFAULT_MESSAGE").toString().replace("{}", profile.getPublicData().get("name").toString());

        }
        profile.getPublicData().replace("name", loginPlatformProfile.getName());
        profile.getPublicData().replace("photoUrl", loginPlatformProfile.getPictureUrl());

        profile.setLoginPlatformData(new LoginPlatformDataDto(loginPlatformProfile, new ArrayList<>()));
        profile = profileService.save(profile);
        return profile;
    }

    @Override
    public Profile createInstantProfile(HttpServletRequest request, LoginData loginData, GamePlatformLink gamePlatformLink) {
        return null;
    }

    @Override
    public User linkGuest(final Profile profile, final LoginData loginData, final GamePlatformLink gamePlatformLink) {

        final Map<String, Object> googleData = verify(loginData, gamePlatformLink);
        final User user = userService.findOne(profile.getUserId());

        final LoginPlatformProfile loginPlatformProfile = new LoginPlatformProfile(googleData.get("id").toString(),
                googleData.get("name").toString(),
                googleData.get("pictureUrl").toString());

        if (googleData.get("email") != null)
            user.setEmail(googleData.getOrDefault("email", "").toString());
        user.setUsername(googleData.get("givenName").toString() + "_" + sequenceService.generateSequence(User.SEQUENCE_NAME));
        if (googleData.get("familyName") != null)
            user.setLastName(googleData.getOrDefault("familyName", "").toString());
        user.setAuthType(LoginPlatformType.Google.getValue());
        user.setFirstName(googleData.get("givenName").toString());

        userService.save(user);

        profile.setLoginPlatformData(new LoginPlatformDataDto(loginPlatformProfile, new ArrayList<>()));
        profile.setLoginPlatformPlayerId(loginPlatformProfile.getId());
        profile.getPublicData().put("name", loginPlatformProfile.getName());
        profile.getPublicData().put("photoUrl", loginPlatformProfile.getPictureUrl());

        profileService.save(profile);

        return user;
    }


    private Map<String, Object> verify(LoginData loginData, GamePlatformLink gamePlatformLink) {
        final Map<String, Object> loginKey = (Map<String, Object>) WiniUtil.loginKeys.get(gamePlatformLink.getId());
        final Map<String, Object> googleLoginKey = (Map<String, Object>) loginKey.get(LoginPlatformType.Google.getValue().toString());
        final Map<String, String> clientTypeKey = (Map<String, String>) googleLoginKey.get(loginData.getAuthData().get("clientType"));
        final String CLIENT_ID = clientTypeKey.get("client_id");
        Map<String, Object> result = new HashMap<>();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(loginData.getAuthData().get("id_token").toString().trim());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            System.out.println(" ====== linkGuest verify ex");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" ====== linkGuest verify ex");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" ====== linkGuest verify ex");
            return result;

        }

        if (idToken != null) {

            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject().trim() + "_" + gamePlatformLink.getGame();

            result.put("id", userId);
            result.put("email", payload.getEmail());
            result.put("name", payload.get("name"));
            result.put("pictureUrl", payload.get("picture"));
            result.put("locale", payload.get("locale"));
            result.put("familyName", payload.get("family_name"));
            result.put("givenName", payload.get("given_name"));
        }

        return result;
    }

}
