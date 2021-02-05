package com.wini.leader_board_integration.data.model.loginPlatform;

import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.info.LoginInfo;
import com.wini.leader_board_integration.data.info.domain.Error;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.vm.LoginVM;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.service.impl.ProfileService;
import com.wini.leader_board_integration.util.WiniUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class FBInstance extends LoginPlatform implements LoginPlatformTypeIn {


    public FBInstance(SecurityService securityService, GamePlatformLinkService gamePlatformLinkService, UserService userService, GameCategoryService gameCategoryService, GameService gameService, FacebookService facebookService, PlayerService playerService, PasswordEncoder passwordEncoder, ProfileService profileService, GuestService guestService) {
        super(securityService, gamePlatformLinkService, userService, gameCategoryService, gameService, facebookService, playerService, passwordEncoder, profileService, guestService);
    }

    @Override
    public int getAuthData() {
        return 1;
    }

    @Override
    public LoginInfo fillProfile(HttpServletRequest request, LoginVM loginVM) {
        final String ip = WiniUtil.getIpAddress(request);
        GamePlatformLink gamePlatformLink = getGamePlatformLinkService().findOne(loginVM.getGamePlatformLinkId());
        LoginInfo loginInfo = new LoginInfo();
        Profile profile = null;
        final String fbToken = (String) loginVM.getLoginData().getAuthData().getOrDefault("signature", null);
        if (fbToken == null) {
            logger.error("loginResult={} LoginType={}", "(fb instant token is empty)", loginVM.getLoginData().getAuthType());
            loginInfo.setError(new Error(ErrorCodeEnum.LOGIN_ERROR.getValue(), "fbInstant token is empty"));
            loginInfo.setResult(null);
            return loginInfo;
        }
        if (!getFacebookService().validateFBInstantToken(fbToken, loginVM)) {
            logger.error("loginResult={} LoginType={}", "(invalid fbInstant token)", loginVM.getLoginData().getAuthType());
            loginInfo.setError(new Error(ErrorCodeEnum.LOGIN_ERROR.getValue(), "invalid fbInstant token"));
            loginInfo.setResult(null);
            return loginInfo;
        }

        try {
            profile = getFacebookService().createInstantProfile(ip, loginVM.getLoginData(), gamePlatformLink);

        } catch (Exception e) {
            logger.error("loginResult={} LoginType={}", "Failed", loginVM.getLoginData().getAuthType());
            logger.warn("login error {} \n stacktrace : \n", e.getMessage());
            e.printStackTrace();
            loginInfo.setError(new Error(ErrorCodeEnum.CREATION_ERROR.getValue(), "Facebook Instant create profile error"));
            loginInfo.setResult(null);
            return loginInfo;
        }
        logger.info("loginResult={} LoginType={}", "Succeed", loginVM.getLoginData().getAuthType());

        return super.intitValue(profile, loginInfo, loginVM, gamePlatformLink);
    }
}
