package com.wini.leader_board_integration.data.model.leaderboard;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@AllArgsConstructor
@Document(value = "archive-board")
public class ArchiveLeaderbord {

    @Id
    private String id;
    private String leaderboardId;
    private String playerId;
    private Integer totalScore;
    private Integer currentScore;
    private String country;
    private String dayOrWeekOfYear;
    private Map<String, Object> configs;
    private Object extraData;
    @JsonIgnore
    @CreatedDate
    private Long createdAt;

    public ArchiveLeaderbord(String leaderboardId, String playerId, Integer totalScore, Integer currentScore, String country) {
        this.leaderboardId = leaderboardId;
        this.playerId = playerId;
        this.totalScore = totalScore;
        this.currentScore = currentScore;
        this.country = country;
    }

    public ArchiveLeaderbord(String leaderboardId, String playerId, Integer totalScore, Integer currentScore, String country, String dayOrWeekOfYear) {
        this.leaderboardId = leaderboardId;
        this.playerId = playerId;
        this.totalScore = totalScore;
        this.currentScore = currentScore;
        this.country = country;
        this.dayOrWeekOfYear = dayOrWeekOfYear;
    }
}
