package com.wini.leader_board_integration.events;

import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class LeaderBoardReportEvent extends ApplicationEvent {

    private final LeaderBoardConfig leaderBoardConfig;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param leaderBoardConfig
     */
    public LeaderBoardReportEvent(Object source, LeaderBoardConfig leaderBoardConfig) {
        super(source);
        this.leaderBoardConfig = leaderBoardConfig;
    }
}
