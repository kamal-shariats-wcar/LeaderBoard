package com.wini.leader_board_integration.data.info;

import com.wini.leader_board_integration.data.dto.ProfileDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kamal on 1/20/2019.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileInfo extends BaseInfo {
    private ProfileDto profile;
}
