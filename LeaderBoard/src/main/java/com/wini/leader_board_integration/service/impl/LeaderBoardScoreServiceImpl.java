package com.wini.leader_board_integration.service.impl;

import com.mongodb.MongoNamespace;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import com.wini.leader_board_integration.data.dto.leaderboard.LeaderBoardSetScoreDB;
import com.wini.leader_board_integration.service.LeaderBoardScoreService;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Repository
@Service
public class LeaderBoardScoreServiceImpl implements LeaderBoardScoreService {
    @Autowired
    MongoTemplate mongoTemplate;
    String dbName;
    @Override
    public void setScore(LeaderBoardSetScoreDB leaderBoardSetScoreDB) {
        MongoCollection<Document> collection = null;
        if (!mongoTemplate.getCollectionNames().contains(leaderBoardSetScoreDB.getLeaderBoardId())) {
            mongoTemplate.createCollection(leaderBoardSetScoreDB.getLeaderBoardId());
        }

        collection = mongoTemplate.getDb().getCollection(leaderBoardSetScoreDB.getLeaderBoardId());
        Document document = collection.find(eq("playerId", leaderBoardSetScoreDB.getPlayerId())).first();
        if (document != null) {
            List<Integer> scores = (List<Integer>) document.get("score");
            scores.add(leaderBoardSetScoreDB.getScore().get(0));
            document.put("finalScore", leaderBoardSetScoreDB.getFinalScore());
            document.put("score", scores);
            mongoTemplate.save(document, leaderBoardSetScoreDB.getLeaderBoardId());
        } else {
            mongoTemplate.save(leaderBoardSetScoreDB, leaderBoardSetScoreDB.getLeaderBoardId());
        }

    }

    @Override
    public FindIterable<Document> getLeaderBoardSetScoreDbs(String leaderBoardId) {
        MongoCollection<Document> collection = null;
        if (!mongoTemplate.getCollectionNames().contains(leaderBoardId)) {
            mongoTemplate.createCollection(leaderBoardId);
        }

        collection = mongoTemplate.getDb().getCollection(leaderBoardId);
        return collection.find();

    }

    @Override
    public void timeOutLeaderBoard(String leaderBoardId, Long startTime , Long endTime) {
        MongoCollection<Document> collection = null;
        if (mongoTemplate.getCollectionNames().contains(leaderBoardId)) {
            if (mongoTemplate.getCollectionNames().contains(leaderBoardId + "-lastWeek")) {
                mongoTemplate.getDb().getCollection(leaderBoardId + "-lastWeek").renameCollection(new MongoNamespace(dbName+"."+leaderBoardId + "-" + startTime +"-" + endTime));
            }//
            mongoTemplate.getDb().getCollection(leaderBoardId).renameCollection(new MongoNamespace(dbName+"."+leaderBoardId + "-lastWeek"));
            mongoTemplate.getDb().createCollection(leaderBoardId);
        }
    }
}
