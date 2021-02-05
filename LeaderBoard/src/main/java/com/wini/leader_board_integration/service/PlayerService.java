package com.wini.leader_board_integration.service;



import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.Profile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface PlayerService {
    public Profile createPlayer(HttpServletRequest request, final GamePlatformLink gamePlatformLink, Map<String, Object> playerRegisterVM);
}
