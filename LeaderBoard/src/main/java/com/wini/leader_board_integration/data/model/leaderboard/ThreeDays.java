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
@Document(value = "threedays-board")
public class ThreeDays extends BaseData implements Serializable {

    private LeaderbordType type = LeaderbordType.THREE_DAYS;

    public ThreeDays(String leaderboardId, String playerId, Integer totalScore, Integer currentScore, String country) {
        super(leaderboardId, playerId, totalScore, currentScore, country);
    }
}
