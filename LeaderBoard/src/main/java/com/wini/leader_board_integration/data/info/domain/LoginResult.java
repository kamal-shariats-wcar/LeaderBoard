package com.wini.leader_board_integration.data.info.domain;

import com.wini.leader_board_integration.data.dto.ProfileDto;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kamal on 1/2/2019.
 */
@Data
@NoArgsConstructor
public class LoginResult {
    private ProfileDto profile;
//    private GameDto game;
}
