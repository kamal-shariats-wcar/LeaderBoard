package com.wini.leader_board_integration.data.model.loginPlatform;

import com.wini.leader_board_integration.data.info.LoginInfo;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.vm.LoginVM;

import javax.servlet.http.HttpServletRequest;

public interface LoginPlatformTypeIn {
    int getAuthData();
    LoginInfo fillProfile(HttpServletRequest request, LoginVM loginVM);
}
