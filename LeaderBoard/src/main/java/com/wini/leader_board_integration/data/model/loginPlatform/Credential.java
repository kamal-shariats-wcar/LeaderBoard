package com.wini.leader_board_integration.data.model.loginPlatform;

import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.info.LoginInfo;
import com.wini.leader_board_integration.data.info.domain.Error;
import com.wini.leader_board_integration.data.model.Game;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.LoginVM;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.service.impl.ProfileService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class Credential extends LoginPlatform implements LoginPlatformTypeIn {


    public Credential(SecurityService securityService, GamePlatformLinkService gamePlatformLinkService, UserService userService, GameCategoryService gameCategoryService, GameService gameService, FacebookService facebookService, PlayerService playerService, PasswordEncoder passwordEncoder, ProfileService profileService, GuestService guestService) {
        super(securityService, gamePlatformLinkService, userService, gameCategoryService, gameService, facebookService, playerService, passwordEncoder, profileService, guestService);
    }

    @Override
    public int getAuthData() {
        return 4;
    }

    @Override
    public LoginInfo fillProfile(HttpServletRequest request, LoginVM loginVM) {
        GamePlatformLink gamePlatformLink = getGamePlatformLinkService().findOne(loginVM.getGamePlatformLinkId());
        Game game = getGameService().findOne(gamePlatformLink.getGame());
        LoginInfo loginInfo = new LoginInfo();
        Profile profile = null;
        if (loginVM.getLoginData().getAuthData().containsKey("operation") && loginVM.getLoginData().getAuthData().get("operation").toString().equalsIgnoreCase("register")) {
            if (getUserService().existUser(loginVM.getLoginData().getAuthData().get("username").toString())) {
                loginInfo.setError(new Error(ErrorCodeEnum.INVALID.getValue(), "username exist"));
                return loginInfo;
            }
            try {
                profile = getPlayerService().createPlayer(request, gamePlatformLink, loginVM.getLoginData().getAuthData());
                String defaultMessage = game.getConfigs().get("SUPPORT_DEFAULT_MESSAGE").toString().replace("{}", profile.getPublicData().get("name").toString());
            } catch (Exception e) {
                logger.error("loginResult={} LoginType={}", "Failed", com.wini.leader_board_integration.data.enums.LoginPlatformType.Credential.getValue());
                logger.warn("login error {} \n stacktrace : \n", e.getMessage());
                e.printStackTrace();
                loginInfo.setResult(null);
                loginInfo.setError(new Error(ErrorCodeEnum.LOGIN_ERROR.getValue(), "can't create credential player profile,error in create credential player"));
                return loginInfo;
            }
        } else {
            User user = getUserService().findByUsername(loginVM.getLoginData().getAuthData().get("username").toString());
            if (user == null || !getPasswordEncoder().matches(loginVM.getLoginData().getAuthData().get("password").toString(), user.getPassword())) {
                loginInfo.setResult(null);
                loginInfo.setError(new Error(ErrorCodeEnum.LOGIN_ERROR.getValue(), "invalid username or password"));
                return loginInfo;
            }
            profile = getProfileService().findOne(user.getProfileId());
        }
        return loginInfo;
    }
}
