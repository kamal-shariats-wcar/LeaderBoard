package com.wini.leader_board_integration.data.info;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kamal on 1/5/2019.
 */
@Data
@NoArgsConstructor
public class GameInfo extends BaseInfo {
    private String id;
    private String gamePlatformLinkId;
}
