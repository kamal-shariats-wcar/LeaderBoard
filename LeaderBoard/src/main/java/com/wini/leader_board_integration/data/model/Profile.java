package com.wini.leader_board_integration.data.model;

import com.wini.leader_board_integration.data.dto.LoginPlatformDataDto;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kamal on 1/1/2019.
 */
@Data
@NoArgsConstructor
@Document(collection = "profile")
public class Profile extends BaseDoc {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private Map<String, Object> publicData = new HashMap<>();
    private Map<String, Object> privateData = new HashMap<>();
    private Map<String, Object> mutableData = new HashMap<>();
    private LoginPlatformDataDto loginPlatformData;
    private String loginPlatformPlayerId;
    private String gamePlatformLinkId;
    private String userId;
    private List<String> displayedNotifications=new ArrayList<>();
    private List<String> displayedNews=new ArrayList<>();
}
