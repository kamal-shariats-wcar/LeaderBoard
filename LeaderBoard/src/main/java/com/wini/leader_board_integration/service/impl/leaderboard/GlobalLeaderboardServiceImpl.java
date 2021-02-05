package com.wini.leader_board_integration.service.impl.leaderboard;

import com.hazelcast.core.HazelcastInstance;

import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.leaderboard.Global;
import com.wini.leader_board_integration.repository.GlobalRepository;
import com.wini.leader_board_integration.service.GlobalLeaderboardService;
import com.wini.leader_board_integration.service.impl.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class GlobalLeaderboardServiceImpl implements GlobalLeaderboardService {


    private final GlobalRepository globalRepository;
    private final MongoTemplate mongoTemplate;
    private final ProfileService profileService;
    private final HazelcastInstance hazelcastInstance;

    @Autowired
    public GlobalLeaderboardServiceImpl(GlobalRepository globalRepository, MongoTemplate mongoTemplate,
                                        ProfileService profileService, HazelcastInstance hazelcastInstance) {

        this.globalRepository = globalRepository;
        this.mongoTemplate = mongoTemplate;
        this.profileService = profileService;
        this.hazelcastInstance = hazelcastInstance;
    }


    @Override
    public void save(Global global) {
//        Global lastGlobal = null;
        synchronized (this) {
            Aggregation aggregation = Aggregation.newAggregation(
                    match(Criteria.where("leaderboardId").is(global.getLeaderboardId())),
                    sort(Sort.by("totalScore").ascending())
            );
            AggregationResults<Global> result = mongoTemplate.aggregate(aggregation, Global.class, Global.class);

            Global existGlobal = result.getMappedResults()
                    .stream().filter(g -> g.getPlayerId().equals(global.getPlayerId()))
                    .findFirst().orElse(null);

            if (result.getMappedResults().size() >= global.getSize()) {
                Global lastGlobal = result.getMappedResults().get(0);
                if (existGlobal == null) {
                    if (global.getTotalScore() >= lastGlobal.getTotalScore()) {
                        globalRepository.delete(lastGlobal);
                        globalRepository.save(global);
                    }
                } else {
                    existGlobal.setTotalScore(global.getTotalScore());
                    globalRepository.save(existGlobal);
                }

            } else {
                if (existGlobal == null) {
                    globalRepository.save(global);
                } else {
                    existGlobal.setTotalScore(global.getTotalScore());
                    globalRepository.save(existGlobal);
                }
            }

        }
    }

    @Override
    public List<RankData> top(String leaderboardId, List<String> keys) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("leaderboardId").is(leaderboardId)),
                sort(Sort.by("totalScore").descending().and(Sort.by("createdAt").descending())),
                project().andExclude("_id")
        );
        AggregationResults<Global> result = mongoTemplate.aggregate(aggregation, Global.class, Global.class);
        int rank = 1;
        List<RankData> rankDataList = new ArrayList<>();
        for (Global global : result.getMappedResults()) {
            Map<String, Object> retKeys = returnKeys(global.getPlayerId(), keys);
            if (!retKeys.isEmpty()) {
                RankData rankData = new RankData(global.getPlayerId(), retKeys, global.getTotalScore(), rank++, null);
                rankDataList.add(rankData);
            }
        }
        rankDataList.sort(Comparator.comparingInt(RankData::getRank));
//        rankDataList.removeIf(rankData -> rankData.getReturnKeys().isEmpty());
        return rankDataList;
    }

    @Override
    public Global findByPlayerId(String playerId) {
        return globalRepository.findByPlayerId(playerId);
    }

    @SuppressWarnings("Duplicates")
    private Map<String, Object> returnKeys(String playerId, List<String> keys) {
        Profile profile;
        Map<String, Object> result = new HashMap<>();
        profile = profileService.findOne(playerId);
        if (profile != null) {
            Map<String, Object> publicData = profile.getPublicData();
            keys.forEach(key -> result.put(key, publicData.get(key)));
        }
        return result;
    }
}
