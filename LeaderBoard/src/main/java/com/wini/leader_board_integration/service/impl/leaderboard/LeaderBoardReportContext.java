package com.wini.leader_board_integration.service.impl.leaderboard;

import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.service.LeaderBoardReport;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LeaderBoardReportContext {
    private final LeaderBoardReport leaderBoardReport;

    public void report(final LeaderBoardConfig leaderBoardConfig) {
        leaderBoardReport.executeReportOperation(leaderBoardConfig);
    }

}
