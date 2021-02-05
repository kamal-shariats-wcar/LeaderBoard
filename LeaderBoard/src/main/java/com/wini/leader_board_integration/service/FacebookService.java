package com.wini.leader_board_integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.model.DefaultConfig;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.LoginVM;
import com.wini.leader_board_integration.data.vm.domain.LoginData;
import com.wini.leader_board_integration.data.vm.domain.LoginPlatformProfile;
import com.wini.leader_board_integration.util.WiniUtil;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by kamal on 1/1/2019.
 */
public interface FacebookService {

    JsonNode getUserInfo(String accessToken);

    Object getUserFriends(String accessToken, String userId);

    Profile createProfile(String ip, String userId, GamePlatformLink gamePlatformLink);

    Profile createInstantProfile(String ip, LoginData loginData, GamePlatformLink gamePlatformLink);

    Profile createFakeInstantProfile(LoginPlatformProfile platformFriend, GamePlatformLink gamePlatformLink, DefaultConfig defaultConfig);

    Profile createFakeFBProfile(LoginPlatformProfile platformFriend, GamePlatformLink gamePlatformLink, DefaultConfig defaultConfig);

    User linkGuest(final Profile profileId, final String token);

    default boolean validateFBInstantToken(String token, LoginVM loginVM) {
        final Map<String, Object> loginKey = (Map<String, Object>) WiniUtil.loginKeys.get(loginVM.getGamePlatformLinkId());
        final Map<String, Object> platformLoginKey = (Map<String, Object>) loginKey.get(LoginPlatformType.FBInstant.getValue().toString());
        final Map<String, String> fb = (Map<String, String>) platformLoginKey.get(loginVM.getLoginData().getAuthData().get("clientType"));
        final String fbAppS = fb.get("app_secret");
        final String[] splits = token.split("\\.");
        final byte[] signature = WiniUtil.base64DecodeToBytes(splits[0]);
        final byte[] dataHash = WiniUtil.hmacSHA256(fbAppS, splits[1]);
        return Arrays.equals(dataHash, signature);
    }
}
