package com.wini.leader_board_integration.data.model.leaderboard;


import com.wini.leader_board_integration.data.model.leaderboard.enums.LeaderbordType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "weekly-board")

public class Weekly extends BaseData implements Serializable {

    private LeaderbordType type = LeaderbordType.WEEKLY;

    public Weekly(String leaderboardId, String playerId, Integer totalScore, Integer currentScore, String country) {
        super(leaderboardId, playerId, totalScore, currentScore, country);
    }
}
