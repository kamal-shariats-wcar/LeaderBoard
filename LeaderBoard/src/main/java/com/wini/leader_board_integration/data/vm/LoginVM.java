package com.wini.leader_board_integration.data.vm;

import com.wini.leader_board_integration.data.vm.domain.LoginData;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kamal on 1/1/2019.
 */
@Data
@NoArgsConstructor
public class LoginVM {
    private LoginData loginData;
    private String gamePlatformLinkId;
}
