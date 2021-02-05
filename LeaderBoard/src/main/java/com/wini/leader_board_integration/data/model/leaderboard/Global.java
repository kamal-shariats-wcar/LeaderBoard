package com.wini.leader_board_integration.data.model.leaderboard;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "global-board")
public class Global {

    @Id
    private String id;
    private String gamePlatformLinkId;
    private String leaderboardId;
    @Indexed
    private String playerId;
    @Indexed(direction = IndexDirection.DESCENDING)
    private Integer totalScore;
    private int size;
    private Object extraData;
    @LastModifiedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    private Long createdAt;

    public Global(String gamePlatformLinkId, String leaderboardId, String playerId, Integer totalScore, int size) {
        this.gamePlatformLinkId = gamePlatformLinkId;
        this.leaderboardId = leaderboardId;
        this.playerId = playerId;
        this.totalScore = totalScore;
        this.size = size;
    }
}
