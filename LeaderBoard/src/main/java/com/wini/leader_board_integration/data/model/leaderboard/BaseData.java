package com.wini.leader_board_integration.data.model.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseData {
    @Id
    private String id;
    private String leaderboardId;
    @Indexed
    private String playerId;
    @Indexed(direction = IndexDirection.DESCENDING)
    private Integer totalScore;
    private Integer lastScore;
    @TextIndexed
    private String country;
    @Field
    @Indexed(name = "expireAtIndex", expireAfterSeconds = 0)
    private LocalDateTime expireAt;
    private Object extraData;
    @JsonIgnore
    @CreatedDate
    @Indexed(direction = IndexDirection.DESCENDING)
    private Long createdAt;

//    private String gameId;

    public BaseData(String leaderboardId, String playerId, Integer totalScore, Integer lastScore, String country/*, String gameId*/) {
        this.leaderboardId = leaderboardId;
        this.playerId = playerId;
        this.totalScore = totalScore;
        this.lastScore = lastScore;
        this.country = country;
//        this.gameId = gameId;
    }
}
