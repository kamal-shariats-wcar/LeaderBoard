package com.wini.leader_board_integration.data.vm;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class BotProfileVM {
    private String MMR;
    private String gameId;
    private String profileId;
    private String contextId;
}
