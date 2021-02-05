package com.wini.leader_board_integration.service;

import com.mongodb.client.FindIterable;
import com.wini.leader_board_integration.data.dto.leaderboard.LeaderBoardSetScoreDB;
import org.bson.Document;

public interface LeaderBoardScoreService {
    void setScore(LeaderBoardSetScoreDB leaderBoardSetScoreDB);
    FindIterable<Document> getLeaderBoardSetScoreDbs (String leaderBoardId);
    void timeOutLeaderBoard(String leaderBoardId, Long startTime , Long endTime);
}
