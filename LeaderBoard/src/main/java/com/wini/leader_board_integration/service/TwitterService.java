package com.wini.leader_board_integration.service;


import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.data.vm.domain.LoginData;

public interface TwitterService {
     Profile createProfile(String ip, LoginData accessToken, GamePlatformLink gamePlatformLink);

     User linkGuest(Profile profile, LoginData loginData, GamePlatformLink gamePlatformLink);
}
