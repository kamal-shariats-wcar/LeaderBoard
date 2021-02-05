package com.wini.leader_board_integration.data.model.loginPlatform;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Data
@Service
public class LoginTypeFactory {
    HashMap<Integer, LoginPlatform> platformMap = new HashMap<Integer, LoginPlatform>();

    public LoginTypeFactory(List<LoginPlatformTypeIn> loginPlatformType) {
        loginPlatformType.forEach(n -> {
            platformMap.put(n.getAuthData(), (LoginPlatform) n);
        });
    }
}
