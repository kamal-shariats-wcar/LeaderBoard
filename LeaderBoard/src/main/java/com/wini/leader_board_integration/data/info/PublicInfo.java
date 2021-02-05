package com.wini.leader_board_integration.data.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kamal on 3/5/2019.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicInfo extends BaseInfo {
    Map<String, Object> result = new LinkedHashMap<>();

    public com.wini.leader_board_integration.data.info.PublicInfo addProperty(String key, Object v) {
        this.getResult().put(key, v);
        return this;
    }
}
