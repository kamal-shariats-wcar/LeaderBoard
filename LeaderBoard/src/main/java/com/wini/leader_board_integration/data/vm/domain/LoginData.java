package com.wini.leader_board_integration.data.vm.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by kamal on 1/2/2019.
 */
@Data
@NoArgsConstructor
public class LoginData {
    private Integer authType;
    private Map<String,Object> authData;
    private LoginPlatformProfile loginPlatformProfile;
    private List<LoginPlatformProfile> loginPlatformFriendsList;
    private Map<String,Object> loginPlatformCustomData;

}
