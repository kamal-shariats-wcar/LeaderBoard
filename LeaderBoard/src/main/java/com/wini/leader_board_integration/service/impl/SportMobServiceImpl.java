package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.dto.LoginPlatformDataDto;
import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.info.PublicInfo;
import com.wini.leader_board_integration.data.model.*;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.domain.LoginData;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.util.WiniUtil;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@Service
public class SportMobServiceImpl implements SportMobService {
    private static final Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.service.impl.SportMobServiceImpl.class);
    private final String sportMobUrl = "https://ws.sportmob.com/v8_3_0/private_api/";
    private final String sportMobToken = "Bearer ktsJPCiUrmZVfkk0cXzL";
    private final UserService userService;
    private final DefaultConfigService defaultConfigService;
    private final ProfileService profileService;
    private final CountryService countryService;
    private final SequenceService sequenceService;
    private final GameService gameService;

    @Value("${support-user}")
    private String supportUser;

    public SportMobServiceImpl(UserService userService, DefaultConfigService defaultConfigService, ProfileService profileService, CountryService countryService, SequenceService sequenceService, GameService gameService) {
        this.userService = userService;
        this.defaultConfigService = defaultConfigService;
        this.profileService = profileService;
        this.countryService = countryService;
        this.sequenceService = sequenceService;
        this.gameService = gameService;
    }

    @Override
    public Profile createProfile(String ip, LoginData loginData, GamePlatformLink gamePlatformLink) {
        Map<String, Object> publicData;
        Profile profile = profileService.findByLoginPlatformPlayerId(loginData.getLoginPlatformProfile().getId());
        if (profile == null) {
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
            profile.setPrivateData(defaultConfig.getPrivateData());
            profile.setMutableData(defaultConfig.getMutableData());


            profile.setPublicData(publicData);
            profile = profileService.save(profile);
            User user = userService.getUser(profile, firstName, LoginPlatformType.SportMob,ip);
            profile.setUserId(user.getUserId());

            Game game = gameService.findOne(gamePlatformLink.getGame());
            String defaultMessage = game.getConfigs().get("SUPPORT_DEFAULT_MESSAGE").toString().replace("{}", profile.getPublicData().get("name").toString());
        }
        profile.getPublicData().replace("name", loginData.getLoginPlatformProfile().getName());
        profile.getPublicData().replace("photoUrl", loginData.getLoginPlatformProfile().getPictureUrl());
        profile.setLoginPlatformData(new LoginPlatformDataDto(loginData.getLoginPlatformProfile(), null));
        profile = profileService.save(profile);
        return profile;

    }

    @Override
    public PublicInfo playerDetail(String ids) {
        final PublicInfo publicInfo = new PublicInfo();
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization",sportMobToken);
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(sportMobUrl+"player/get-detail").queryParam("ids", ids);
        final HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);

        try {
            final ResponseEntity<String> response = restTemplate.exchange(
                    uriComponentsBuilder.toUriString(),
                    HttpMethod.GET,
                    httpEntity,
                    String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                final JSONObject jsonResult = new JSONObject(response.getBody());
                if (jsonResult.has("code") && jsonResult.getInt("code") == 200 && jsonResult.has("data")) {
                    publicInfo.addProperty("data", jsonResult.getJSONArray("data").toString());
                } else {
                    logger.warn("invalid request to sportMob {}", ids);
                }
            }
        } catch (Exception ex) {
            logger.warn("sportMob request unsuccessful {}", ids);
        }
        return publicInfo;
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
}
