package com.wini.leader_board_integration.service;


import com.wini.leader_board_integration.data.info.PublicInfo;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.vm.domain.LoginData;

public interface SportMobService {
    Profile createProfile(final String ip, final LoginData loginData, final GamePlatformLink gamePlatformLink);

    PublicInfo playerDetail(String id);
}
