package com.wini.leader_board_integration.data.info;

import com.wini.leader_board_integration.data.info.domain.Error;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by kamal on 1/1/2019.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseInfo {
    private Error error;
}
