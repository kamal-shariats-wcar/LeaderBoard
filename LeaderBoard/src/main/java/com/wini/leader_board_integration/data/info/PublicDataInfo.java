package com.wini.leader_board_integration.data.info;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * Created by kamal on 1/19/2019.
 */
@Data
@NoArgsConstructor
public class PublicDataInfo extends BaseInfo {
    private HashMap<String, Object> result = new HashMap<>();
}
