package com.wini.leader_board_integration.data.model.loginPlatform;

import com.wini.leader_board_integration.data.dto.GameDto;
import com.wini.leader_board_integration.data.dto.GamePlatformLinkDto;
import com.wini.leader_board_integration.data.dto.ProfileDto;
import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.info.LoginInfo;
import com.wini.leader_board_integration.data.info.domain.Error;
import com.wini.leader_board_integration.data.model.Game;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.LoginVM;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.service.impl.ProfileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Data
public class LoginPlatform {
    Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.decorator.impl.LoginDecoratorServiceImpl.class);
    int authType;
    String authName;
    private final SecurityService securityService;
    private final GamePlatformLinkService gamePlatformLinkService;
    private final UserService userService;
    private final GameCategoryService gameCategoryService;
    private final GameService gameService;
    private final FacebookService facebookService;
    private final PlayerService playerService;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;
    private final GuestService guestService;

    @Value("${CDN_BASE_URL}")
    private String cdnBaseUrl;

    public LoginInfo intitValue(Profile profile, LoginInfo loginInfo, LoginVM loginVM, GamePlatformLink gamePlatformLink) {
        MDC.put("gamePlatformLink", loginVM.getGamePlatformLinkId());
        if (gamePlatformLink == null) {
            loginInfo.setError(new Error(ErrorCodeEnum.GAME_PLATFORM_LINK.getValue(), "gamePlatformLink not found"));
            return loginInfo;
        }
        Game game = gameService.findOne(gamePlatformLink.getGame());
        final User user = userService.findOne(profile.getUserId());
        Map<String, Object> publicData = profile.getPublicData();
        publicData.put("username", user.getUsername());
        Integer rank = -1;
        publicData.put("rank", rank);

        profile.setPublicData(publicData);
        loginInfo.getResult().put("profile", new ProfileDto(profile));
        gamePlatformLink.getConfigs().remove("clientIds");
        GamePlatformLinkDto gamePlatformLinkDto = new GamePlatformLinkDto(gamePlatformLink);
        gamePlatformLinkDto.getConfigs().remove("loginKeys");
        gamePlatformLinkDto.getConfigs().remove("paymentKeys");
        loginInfo.getResult().put("gamePlatformLink", gamePlatformLinkDto);

        GameDto gameDto = new GameDto(game);
        loginInfo.getResult().put("game", gameDto);
        loginInfo.getResult().put("gameCategories", gameCategoryService.findByGamePlatformLinkId(profile.getGamePlatformLinkId()));
        loginInfo.setToken(securityService.getUserToken(user.getUsername()));
        loginInfo.getResult().put("cdnBaseUrl", cdnBaseUrl);
        if (loginVM.getLoginData().getAuthType().equals(LoginPlatformType.FBInstant.getValue()))
            loginInfo.getResult().put("loginPlatformPlayerId", loginVM.getLoginData().getLoginPlatformProfile().getId());
        MDC.put("playerId", profile.getId());

        logger.info("LoginType={} gamePlatformLink={}", loginVM.getLoginData().getAuthType(), loginVM.getGamePlatformLinkId());
        if (!profile.getGamePlatformLinkId().equals(gamePlatformLink.getId())) {
            loginInfo.setError(new Error(ErrorCodeEnum.NOT_MATCH.getValue(), "profile with gamePlatformLink doesn't match"));
            return loginInfo;
        }
        return loginInfo;
    }
}
