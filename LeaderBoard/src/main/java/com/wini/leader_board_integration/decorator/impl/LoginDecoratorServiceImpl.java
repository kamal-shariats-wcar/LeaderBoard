package com.wini.leader_board_integration.decorator.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.wini.leader_board_integration.data.dto.GameDto;
import com.wini.leader_board_integration.data.dto.GamePlatformLinkDto;
import com.wini.leader_board_integration.data.dto.ProfileDto;
import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.info.LoginInfo;
import com.wini.leader_board_integration.data.info.PublicInfo;
import com.wini.leader_board_integration.data.info.domain.Error;
import com.wini.leader_board_integration.data.model.Game;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.loginPlatform.LoginPlatformTypeIn;
import com.wini.leader_board_integration.data.model.loginPlatform.LoginTypeFactory;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.LoginVM;
import com.wini.leader_board_integration.decorator.LoginDecoratorService;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.service.impl.ProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kamal on 1/2/2019.
 */
@SuppressWarnings("Duplicates")
@Service
@RequiredArgsConstructor
public class LoginDecoratorServiceImpl implements LoginDecoratorService {
    Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.decorator.impl.LoginDecoratorServiceImpl.class);


    private final FacebookService facebookService;
    private final TwitterService twitterService;
    private final GuestService guestService;
    private final GamePlatformLinkService gamePlatformLinkService;
    private final ProfileService profileService;
    private final SecurityService securityService;
    private final GoogleService googleService;
    private final HazelcastInstance hazelcastInstance;
    private final UserService userService;
    private final GameService gameService;
    private final GameCategoryService gameCategoryService;
    private final PlayerService playerService;
    private final SportMobService sportMobService;
    private boolean outOfService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LoginTypeFactory LoginTypeFactory;

    @Value("${CDN_BASE_URL}")
    private String cdnBaseUrl;
    @Value("${support-password}")
    private String supportPassword;

    @Value("${create-guest}")
    private Boolean createGuest;

    @Value("${support-user}")
    private String supportUser;


    @Override
    public LoginInfo login(HttpServletRequest request, LoginVM loginVM) {
        LoginInfo loginInfo = new LoginInfo();
        LoginPlatformTypeIn loginPlatform = (LoginPlatformTypeIn) LoginTypeFactory.getPlatformMap().get(loginVM.getLoginData().getAuthType());
        if (loginPlatform == null) {
            loginInfo.setError(new Error(ErrorCodeEnum.LOGIN_ERROR.getValue(), "invalid login authType"));
            return loginInfo;
        }
        return loginPlatform.fillProfile(request, loginVM);
    }

    @Override
    public PublicInfo getOutOfService() {
        final PublicInfo publicInfo = new PublicInfo();
        outOfService = true;
        final ITopic<String> iTopic = hazelcastInstance.getTopic("resetConfig");
        iTopic.publish("outOfService");
        publicInfo.getResult().put("outOfService", outOfService);
        return publicInfo;
    }

    @Override
    public PublicInfo backToService() {
        final PublicInfo publicInfo = new PublicInfo();
        outOfService = false;
        final ITopic<String> iTopic = hazelcastInstance.getTopic("resetConfig");
        iTopic.publish("backToService");
        publicInfo.getResult().put("outOfService", outOfService);
        logger.info("server back to the service");
        return publicInfo;
    }

    @Override
    public PublicInfo findProfileIdByFacebookId(String facebookId) {
        final PublicInfo publicInfo = new PublicInfo();
        Profile profile = profileService.findByFacebookId(facebookId);
        publicInfo.getResult().put("profileId", profile.getId());
        publicInfo.getResult().put("publicData", profile.getPublicData());
        return publicInfo;
    }

    @Override
    public PublicInfo userExist(String username) {
        PublicInfo publicInfo = new PublicInfo();
        if (username != null && !username.isEmpty() && username.length() > 4 && !userService.existUser(username)) {
            publicInfo.getResult().put("valid", Boolean.TRUE);
        } else {
            publicInfo.getResult().put("valid", Boolean.FALSE);
        }
        return publicInfo;
    }

    @Override
    public PublicInfo linkGuest(LoginVM loginVM, String token) {
        PublicInfo publicInfo = new PublicInfo();
        final Profile profile = profileService.findByToken(token);
        final GamePlatformLink gamePlatformLink = gamePlatformLinkService.findOne(profile.getGamePlatformLinkId());
        if (loginVM.getLoginData().getAuthType().equals(LoginPlatformType.FB.getValue())) {
            Profile existProfile = profileService.findByLoginPlatformPlayerId(loginVM.getLoginData().getAuthData().get("userID").toString());
            if (existProfile != null) {
                publicInfo.setError(new Error(ErrorCodeEnum.PLAYER_LINKED.getValue(), "profile exist and linked"));
                return publicInfo;
            }
        } else if (loginVM.getLoginData().getAuthType().equals(LoginPlatformType.Google.getValue()) || loginVM.getLoginData().getAuthType().equals(LoginPlatformType.Twitter.getValue())) {
            final String loginPlatformPlayerId = loginVM.getLoginData().getLoginPlatformProfile().getId().trim() + "_" + gamePlatformLink.getGame();

            Profile existProfile = profileService.findByLoginPlatformPlayerId(loginPlatformPlayerId);
            if (existProfile != null) {
                publicInfo.setError(new Error(ErrorCodeEnum.PLAYER_LINKED.getValue(), "profile exist and linked"));
                return publicInfo;
            }
        }

        User user;
        if (loginVM.getLoginData().getAuthType().equals(LoginPlatformType.FB.getValue())) {
            user = facebookService.linkGuest(profile, loginVM.getLoginData().getAuthData().get("accessToken").toString());
        } else if (loginVM.getLoginData().getAuthType().equals(LoginPlatformType.Google.getValue())) {
            user = googleService.linkGuest(profile, loginVM.getLoginData(), gamePlatformLink);
        } else if (loginVM.getLoginData().getAuthType().equals(LoginPlatformType.Twitter.getValue())) {
            user = twitterService.linkGuest(profile, loginVM.getLoginData(), gamePlatformLink);
        } else {
            publicInfo.setError(new Error(ErrorCodeEnum.INVALID.getValue(), ErrorCodeEnum.INVALID.getDesc()));
            return publicInfo;
        }
        publicInfo.getResult().put("token", securityService.getUserToken(user.getUsername()));
        publicInfo.getResult().put("authType", user.getAuthType());
        publicInfo.getResult().put("profile", profile);
        return publicInfo;
    }

    @Override
    public LoginInfo generateToken(final String profileId) {
        final LoginInfo loginInfo = new LoginInfo();
        final Profile profile = profileService.findOne(profileId);
        if (Objects.nonNull(profile)) {
            try {
                final User user = userService.findOne(profile.getUserId());
                loginInfo.setToken(securityService.getUserToken(user.getUsername()));
                final GamePlatformLink gamePlatformLink = gamePlatformLinkService.findOne(profile.getGamePlatformLinkId());
                final Map<String, Object> publicData = profile.getPublicData();
                Integer rank = -1;
                publicData.put("rank", rank);
                profile.setPublicData(publicData);
                loginInfo.getResult().put("profile", new ProfileDto(profile));
                gamePlatformLink.getConfigs().remove("clientIds");
                GamePlatformLinkDto gamePlatformLinkDto = new GamePlatformLinkDto(gamePlatformLink);
                gamePlatformLinkDto.getConfigs().remove("loginKeys");
                gamePlatformLinkDto.getConfigs().remove("paymentKeys");
                loginInfo.getResult().put("gamePlatformLink", gamePlatformLinkDto);
                final Game game = gameService.findOne(gamePlatformLink.getGame());
                final GameDto gameDto = new GameDto(game);
                loginInfo.getResult().put("game", gameDto);
                loginInfo.getResult().put("gameCategories", gameCategoryService.findByGamePlatformLinkId(profile.getGamePlatformLinkId()));
            } catch (Exception ex) {
                loginInfo.setError(new Error(ErrorCodeEnum.LOGIN_ERROR.getValue(), ErrorCodeEnum.LOGIN_ERROR.getDesc()));
            }
        } else {
            loginInfo.setError(new Error(ErrorCodeEnum.NOT_FOUND.getValue(), "profile not found"));
        }
        return loginInfo;
    }


}
