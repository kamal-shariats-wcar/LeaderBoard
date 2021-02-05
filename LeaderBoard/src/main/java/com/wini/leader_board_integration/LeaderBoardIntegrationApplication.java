package com.wini.leader_board_integration;

import com.hazelcast.core.HazelcastInstance;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.service.GamePlatformLinkService;
import com.wini.leader_board_integration.service.LeaderBoardConfigService;
import com.wini.leader_board_integration.service.LeaderBoardScoreService;
import com.wini.leader_board_integration.service.impl.ProfileService;
import com.wini.leader_board_integration.util.WiniUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = HazelcastAutoConfiguration.class)
@EnableSwagger2
@EnableCaching
@EnableScheduling
@EnableMongoAuditing
public class LeaderBoardIntegrationApplication {

    @Autowired
    LeaderBoardScoreService leaderBoardScoreService;

    @Autowired
    HazelcastInstance hazelcastInstance;
    @Autowired
    ProfileService profileService;

    @Autowired
    private GamePlatformLinkService gamePlatformLinkService;

    @Autowired
    private LeaderBoardConfigService leaderBoardConfigService;

    public static void main(String[] args) {
        SpringApplication.run(LeaderBoardIntegrationApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {

        try {
            getLeaderboardConfig();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    public void getLeaderboardConfig() {
        List<String> gamePlatformIdList = new ArrayList<>();
        gamePlatformLinkService.findAll().stream().forEach(gamePlatformLink -> {

            WiniUtil.gamePlatformLinksConfigs.put(gamePlatformLink.getId(), gamePlatformLink.getConfigs());
            WiniUtil.loginKeys.put(gamePlatformLink.getId(), gamePlatformLink.getConfigs().get("loginKeys"));
            WiniUtil.paymentKeys.put(gamePlatformLink.getId(), gamePlatformLink.getConfigs().get("paymentKeys"));
            gamePlatformIdList.add(gamePlatformLink.getId());
        });
        gamePlatformIdList.forEach(id -> hazelcastInstance.getMap("leaderboardConfig").put(id, leaderBoardConfigService.findByGamePlatformLinkId(id)));
        List<LeaderBoardConfig> boardConfigs = leaderBoardConfigService.findAll();
        boardConfigs.forEach(c -> {

        });

    }
}
