package com.wini.leader_board_integration.data.info;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kamal on 1/2/2019.
 */
@Data
@NoArgsConstructor
public class LoginInfo extends BaseInfo {
    private Map<String,Object> result=new HashMap<>();
    private String token;
}
