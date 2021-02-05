package com.wini.leader_board_integration.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
public class DefaultConfigDto {
    private Map<String,Object> publicData;
    private Map<String,Object> privateData;
    private Map<String,Object> mutableData;
}
