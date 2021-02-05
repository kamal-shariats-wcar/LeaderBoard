package com.wini.leader_board_integration.service;



import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by kamal on 1/8/2019.
 */
public interface GuestService {

    Profile createGuest(HttpServletRequest request, GamePlatformLink gamePlatformLink, String ip);

}
