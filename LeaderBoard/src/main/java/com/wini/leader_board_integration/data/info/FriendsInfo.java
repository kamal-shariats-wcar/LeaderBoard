package com.wini.leader_board_integration.data.info;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * Created by kamal on 2/16/2019.
 */
@Data
@NoArgsConstructor
public class FriendsInfo extends BaseInfo {
    HashMap<String, Object> result = new HashMap<>();
}
