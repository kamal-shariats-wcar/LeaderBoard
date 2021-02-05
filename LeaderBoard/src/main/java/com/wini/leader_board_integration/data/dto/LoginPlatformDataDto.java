package com.wini.leader_board_integration.data.dto;

import com.wini.leader_board_integration.data.vm.domain.LoginPlatformProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginPlatformDataDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private LoginPlatformProfile loginPlatformProfile;
    private List<LoginPlatformProfile> loginPlatformFriendsList;
}
