package com.wini.leader_board_integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.domain.LoginData;


import javax.servlet.http.HttpServletRequest;

public interface GoogleService {

    JsonNode getUserInfo(String accessToken);

    Object getUserFriends(String accessToken, String userId);

    Profile createProfile(String ip, LoginData loginData, GamePlatformLink gamePlatformLink);

    Profile createInstantProfile(HttpServletRequest request, LoginData loginData, GamePlatformLink gamePlatformLink);

    User linkGuest(Profile profile, LoginData loginData, GamePlatformLink gamePlatformLink);
}
