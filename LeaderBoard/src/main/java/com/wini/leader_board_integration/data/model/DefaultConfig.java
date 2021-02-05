package com.wini.leader_board_integration.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

/**
 * Created by kamal on 1/15/2019.
 */
@Data
@NoArgsConstructor
@Document(collection = "defaultConfigs")
public class DefaultConfig extends BaseDoc {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String gamePlatformLinkId;
    private Map<String,Object> publicData;
    private Map<String,Object> privateData;
    private Map<String,Object> mutableData;
}
