package com.wini.leader_board_integration.service.impl.leaderboard;

import com.wini.leader_board_integration.service.LeaderboardBaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardHolder {

    private final List<LeaderboardBaseService> services;

    public LeaderboardHolder(List<LeaderboardBaseService> services) {
        this.services = services;
    }

    public LeaderboardBaseService getService(int everyHours) {
        return services
                .stream()
                .filter(leaderboardBaseService -> leaderboardBaseService.getLeaderboardType() == everyHours ||
                        (everyHours >= 1000 && leaderboardBaseService.getLeaderboardType() >= everyHours))
                .findFirst().orElse(null);
    }

}
