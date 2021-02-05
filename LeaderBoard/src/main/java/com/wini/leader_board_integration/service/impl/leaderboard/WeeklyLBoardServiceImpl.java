package com.wini.leader_board_integration.service.impl.leaderboard;

import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.data.model.leaderboard.Weekly;
import com.wini.leader_board_integration.exception.BusinessException;
import com.wini.leader_board_integration.repository.WeeklyRepository;
import com.wini.leader_board_integration.service.LeaderboardBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@SuppressWarnings("Duplicates")

@Service
public class WeeklyLBoardServiceImpl extends LeaderboardBaseService<Weekly> {


    private final WeeklyRepository weeklyRepository;
    private final MongoTemplate mongoTemplate;


    @Autowired
    public WeeklyLBoardServiceImpl(WeeklyRepository weeklyRepository, MongoTemplate mongoTemplate) {
        this.weeklyRepository = weeklyRepository;
        this.mongoTemplate = mongoTemplate;

    }


    @Override
    public Weekly save(Weekly weekly) {
        return weeklyRepository.save(weekly);
    }

    @Override
    public Weekly findLatest(String leaderboardId, String playerId) {
        return weeklyRepository.findDistinctTopByLeaderboardIdAndPlayerIdOrderByCreatedAtDescExpireAtDesc(leaderboardId, playerId);
    }

    @Override
    public RankData rankFor(String leaderboardId, String playerId, List<String> keys, boolean includeCountry) {
        /*
         * current rank for specific player in specific leaderboard.
         */

        Criteria gtCriteria, eqCriteria, allCriteria = new Criteria();
        String country;

        /*
         *   Find latest score for current player
         */
        Weekly currentPlayer = weeklyRepository.findDistinctTopByLeaderboardIdAndPlayerIdOrderByCreatedAtDescExpireAtDesc(leaderboardId, playerId);

        GroupOperation groupBy = Aggregation.group("playerId", "createdAt", "totalScore");

        GroupOperation secondGroup = Aggregation.group("_id.playerId")
                .first("_id.totalScore").as("totalScore")
                .first("_id.createdAt").as("createdAt");

        if (currentPlayer != null) {
            gtCriteria = Criteria
                    .where("totalScore").gt(currentPlayer.getTotalScore())
                    .and("playerId").nin(playerId);

            eqCriteria = Criteria
                    .where("totalScore").is(currentPlayer.getTotalScore())
                    .and("playerId").nin(playerId)
                    .and("createdAt").gt(currentPlayer.getCreatedAt());

            country = currentPlayer.getCountry();
            if (includeCountry) {
                gtCriteria.and("country").is(country);
                eqCriteria.and("country").is(country);
                groupBy = Aggregation.group("playerId", "createdAt", "totalScore", "country");
                secondGroup = secondGroup.first("_id.country").as("country");
            }

            allCriteria.orOperator(gtCriteria, eqCriteria);

            Aggregation aggregation = Aggregation.newAggregation(
                    groupBy,
                    sort(Sort.by("createdAt").descending()),
                    secondGroup,
                    match(allCriteria)
            );

            AggregationResults<Weekly> result = mongoTemplate.aggregate(aggregation, Weekly.class, Weekly.class);

            int rank = result.getMappedResults().size() + 1;

            return new RankData(playerId, returnKeys(currentPlayer.getPlayerId(),
                    keys), currentPlayer.getTotalScore(), rank, currentPlayer.getCreatedAt());
        }
        return null;
    }

    @Override
    public List<RankData> top(String leaderboardId, String country, int size) {

        LeaderBoardConfig boardDB = getLeaderboard(leaderboardId)
                .orElseThrow(() -> new BusinessException(ErrorCodeEnum.NOT_FOUND.getValue(), "leaderboard not found"));

        GroupOperation groupBy = Aggregation.group("playerId", "createdAt", "totalScore");

        GroupOperation secondGroup = Aggregation.group("_id.playerId")
                .first("_id.totalScore").as("totalScore")
                .first("_id.createdAt").as("createdAt");

        Criteria criteria = Criteria.where("leaderboardId").is(leaderboardId);

        if (!country.isEmpty()) {
            groupBy = Aggregation.group("playerId", "createdAt", "totalScore", "country");
            criteria.and("country").is(country);
            secondGroup.first("_id.country").as("country");
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                groupBy,
                sort(Sort.by("createdAt").descending()),
                secondGroup,
                sort(Sort.by("totalScore").descending().and(Sort.by("createdAt").descending())),
                project()
                        .and("_id").as("playerId")
                        .and("totalScore").as("totalScore"),
                limit(size)
        );
        AggregationResults<Weekly> result = mongoTemplate.aggregate(aggregation, Weekly.class, Weekly.class);


        int rank = 1;
        List<RankData> rankDataList = new ArrayList<>();
        for (Weekly weekly : result.getMappedResults()) {
            Map<String, Object> retKeys = returnKeys(weekly.getPlayerId(), boardDB.getReturnKeys());
            if (!retKeys.isEmpty()) {
                RankData rankData = new RankData(weekly.getPlayerId(), retKeys, weekly.getTotalScore(), rank++, null);
                rankDataList.add(rankData);
            }
        }
        rankDataList.sort(Comparator.comparingInt(RankData::getRank));
        return rankDataList;
    }


    @Override
    public List<RankData> topMe(String leaderboardId, RankData playerRank, List<String> keys, boolean includeCountry, int size) {

        if (playerRank == null)
            return new ArrayList<>();
        /*
          top players for specific player in specific leaderboard.
         */

        GroupOperation groupBy = Aggregation.group("playerId", "createdAt", "totalScore");

        GroupOperation secondGroup = Aggregation.group("_id.playerId")
                .first("_id.totalScore").as("totalScore")
                .first("_id.createdAt").as("createdAt");

        Criteria criteria = Criteria
                .where("leaderboardId").is(leaderboardId);


        Criteria sameScore = Criteria.where("createdAt").gt(playerRank.getLastDateScore())
                .and("totalScore").is(playerRank.getScore());
        Criteria higherScore = Criteria.where("totalScore").gt(playerRank.getScore());

        if (includeCountry) {
            String country = String.valueOf(returnKeys(playerRank.getId(), Collections.singletonList("country")).get("country"));
            criteria.and("country").is(country);
            higherScore.and("country").is(country);
            sameScore.and("country").is(country);
            groupBy = Aggregation.group("playerId", "createdAt", "totalScore", "country");
            secondGroup = secondGroup.first("_id.country").as("country");
        }
        Aggregation topAggregation = Aggregation.newAggregation(
                match(criteria),
                groupBy,
                sort(Sort.by("createdAt").descending()),
                secondGroup,
                match(Criteria.where("totalScore").gte(playerRank.getScore())),
                sort(Sort.by("totalScore").ascending().and(Sort.by("createdAt").ascending())),
                match(new Criteria().orOperator(sameScore, higherScore)),
                project()
                        .and("_id").as("playerId")
                        .and("totalScore").as("totalScore"),
                limit(size)
        );
        AggregationResults<Weekly> result = mongoTemplate.aggregate(topAggregation, Weekly.class, Weekly.class);


        List<RankData> rankDataList = new ArrayList<>();
        int pRank = playerRank.getRank();

        for (Weekly weekly : result.getMappedResults()) {
            if (!weekly.getPlayerId().equals(playerRank.getId())) {
                Map<String, Object> retKeys = returnKeys(weekly.getPlayerId(), keys);
                if (!retKeys.isEmpty()) {
                    RankData rankData = new RankData(weekly.getPlayerId(), retKeys, weekly.getTotalScore(), --pRank, null);
                    rankDataList.add(rankData);
                }
            }
        }
        Collections.reverse(rankDataList);
        return rankDataList;
    }

    @Override
    public List<RankData> bottomMe(String leaderboardId, RankData playerRank, List<String> keys, boolean includeCountry, int size) {

        if (playerRank == null)
            return new ArrayList<>();

        /*
         * bottom players for specific player in specific leaderboard.
         */

        GroupOperation groupBy = Aggregation.group("playerId", "createdAt", "totalScore");

        GroupOperation secondGroup = Aggregation.group("_id.playerId")
                .first("_id.totalScore").as("totalScore")
                .first("_id.createdAt").as("createdAt");

        Criteria criteria = Criteria
                .where("leaderboardId").is(leaderboardId);


        Criteria sameScore = Criteria.where("createdAt")
                .lt(playerRank.getLastDateScore())
                .and("totalScore").is(playerRank.getScore());

        Criteria higherScore = Criteria.where("totalScore").lt(playerRank.getScore());

        if (includeCountry) {
            String country = String.valueOf(returnKeys(playerRank.getId(), Collections.singletonList("country")).get("country"));
            criteria.and("country").is(country);
            sameScore.and("country").is(country);
            higherScore.and("country").is(country);
            groupBy = Aggregation.group("playerId", "createdAt", "totalScore", "country");
            secondGroup = Aggregation.group("_id.playerId")
                    .first("_id.totalScore").as("totalScore")
                    .first("_id.createdAt").as("createdAt")
                    .first("_id.country").as("country");
        }
        Aggregation bottomAggregation = Aggregation.newAggregation(
                match(criteria),
                groupBy,
                sort(Sort.by("createdAt").descending()),
                secondGroup,
                match(Criteria.where("totalScore").lte(playerRank.getScore())),
                sort(Sort.by("totalScore").descending().and(Sort.by("createdAt").descending())),
                match(new Criteria().orOperator(sameScore, higherScore)),
                project()
                        .and("_id").as("playerId")
                        .and("totalScore").as("totalScore"),
                limit(size)
        );

        AggregationResults<Weekly> result = mongoTemplate.aggregate(bottomAggregation, Weekly.class, Weekly.class);


        int pRank = playerRank.getRank();
        List<RankData> rankDataList = new ArrayList<>();
        for (Weekly weekly : result.getMappedResults()) {
            if (!weekly.getPlayerId().equals(playerRank.getId())) {
                Map<String, Object> retKeys = returnKeys(weekly.getPlayerId(), keys);
                if (!retKeys.isEmpty()) {
                    RankData rankData = new RankData(weekly.getPlayerId(), retKeys, weekly.getTotalScore(), ++pRank, null);
                    rankDataList.add(rankData);
                }
            }
        }
        return rankDataList;
    }


    @Override
    public int getLeaderboardType() {
        return 168;
    }


}
