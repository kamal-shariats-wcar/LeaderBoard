package com.wini.leader_board_integration.decorator.impl;



import com.wini.leader_board_integration.config.security.JwtTokenUtil;
import com.wini.leader_board_integration.data.dto.TempItem;
import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.dto.leaderboard.ScoreDto;
import com.wini.leader_board_integration.data.enums.LeaderBoardType;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.leaderboard.*;
import com.wini.leader_board_integration.data.model.leaderboard.enums.LeaderBoardSubmitScoreType;
import com.wini.leader_board_integration.decorator.LeaderboardDecorator;
import com.wini.leader_board_integration.decorator.ProfileDecorator;
import com.wini.leader_board_integration.exception.BusinessException;
import com.wini.leader_board_integration.service.ArchiveLeaderboardService;
import com.wini.leader_board_integration.service.GlobalLeaderboardService;
import com.wini.leader_board_integration.service.LeaderBoardConfigService;
import com.wini.leader_board_integration.service.LeaderboardBaseService;
import com.wini.leader_board_integration.service.impl.ProfileService;
import com.wini.leader_board_integration.util.WiniUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@SuppressWarnings({"Duplicates", "unchecked"})
@Service
public class LeaderboardDecoratorImpl implements LeaderboardDecorator {


    private Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.decorator.impl.LeaderboardDecoratorImpl.class);

    private static Predicate<Integer> isDaily = everyHours -> everyHours == 24;
    private static Predicate<Integer> isWeekly = everyHours -> everyHours == 168;
    private static Predicate<Integer> isThreeDays = everyHours -> everyHours == 72;
    private static Predicate<Integer> isHour = everyHours -> everyHours >= 1000;


    private static Predicate<Long> isStarted = startedTime -> startedTime < WiniUtil.toEpochSecond();
    private static BiPredicate<List<RankData>, String> existInTop = (topList, player) ->
            topList.stream().map(RankData::getId).anyMatch(id -> id.equals(player));

    @Autowired
    private List<LeaderboardBaseService> leaderboardBaseServices;

    @Autowired
    private LeaderBoardConfigService leaderBoardConfigService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileDecorator profileDecorator;

    @Autowired
    private ArchiveLeaderboardService archiveLeaderboardService;

    @Autowired
    private GlobalLeaderboardService globalLeaderboardService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public LeaderBoardConfig save(LeaderBoardConfig leaderBoardConfig) {
        return leaderBoardConfigService.save(leaderBoardConfig);
    }

    @Override
    public void delete(String leaderboardId) {
        leaderBoardConfigService.delete(leaderboardId);
    }

    @Override
    public List<RankData> aroundMe(String leaderboardId, String playerId, boolean includeCountry) {
        LeaderBoardConfig boardDB = leaderBoardConfigService.findOne(leaderboardId);
        LeaderboardBaseService leaderboardBaseService = getService(boardDB.getEveryHours());

        RankData playerRank = null;

        String playerCountry = "";
        if (includeCountry) {
            playerCountry = String.valueOf(profileService.findOne(playerId).getPublicData().get("country"));
        }
        List<RankData> top = leaderboardBaseService.top(leaderboardId, playerCountry, boardDB.getTopCount());
        List<RankData> entries = new ArrayList<>(top);

        if (!existInTop.test(top, playerId)) {
            playerRank = leaderboardBaseService.rankFor(leaderboardId, playerId, boardDB.getReturnKeys(), includeCountry);
            if (playerRank != null) {
                List<RankData> topMe = leaderboardBaseService.topMe(boardDB.getId(), playerRank, boardDB.getReturnKeys(), includeCountry, boardDB.getAroundMeCount());
                entries.addAll(topMe);
            }
        }

        if (playerRank == null)
            playerRank = leaderboardBaseService.rankFor(leaderboardId, playerId, boardDB.getReturnKeys(), includeCountry);

        List<RankData> bottom = leaderboardBaseService
                .bottomMe(boardDB.getId(), playerRank, boardDB.getReturnKeys(), includeCountry, boardDB.getAroundMeCount());
        entries.add(playerRank);

        if (!bottom.isEmpty()) {
            int fromIndex = boardDB.getTopCount() - (playerRank == null ? 0 : playerRank.getRank());
            if (fromIndex < 0 || bottom.size() < fromIndex)
                fromIndex = 0;
            entries.addAll(bottom.subList(fromIndex, bottom.size()));
        }
//        entries.removeIf(entry -> entry.getReturnKeys().isEmpty());
        return entries.stream().filter(Objects::nonNull).filter(WiniUtil.distinctByKey(RankData::getId)).collect(Collectors.toList());
    }

    @Override
    public void setScore(ScoreDto scoreDto) {
        Daily latestDaily;
        Weekly latestWeekly;
        ThreeDays latestThreeDays;
        Hour latestHour;
        Integer globalScore = 0;
        Map<String, Object> publicData;
        Integer oldScore;
        String dayOrWeekOfYear = "";

        Profile profile = profileService.findOne(scoreDto.getPlayerId());
        BusinessException.throwIfNull(profile);

        publicData = profile.getPublicData();

        Map<String, List<TempItem>> tempItems = (Map<String, List<TempItem>>) profile.getPrivateData()
                .getOrDefault("tempItems", null);
        if (tempItems != null) {
            try {
                tempItems = tempItems.entrySet()
                        .stream()
                        .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().stream()
                                .map(tempItem -> tempItem.getCount() == 0 ? tempItem.updateCount() : tempItem)
                                .collect(Collectors.toList())))
                        .collect(Collectors.toMap(e -> e.getKey(), e -> (List<TempItem>) e.getValue()));
                profile.getPrivateData().put("tempItems", tempItems);
                profileService.save(profile);

            } catch (Exception e) {
                logger.error(" TempItem Exception profileId,{}", scoreDto.getPlayerId());
                e.printStackTrace();
            }

        }

        /**
         * find all leaderboards-config
         */
        List<LeaderBoardConfig> boardDBS = leaderBoardConfigService.findInList(scoreDto.getLeaderboards());

        for (LeaderBoardConfig boardConfig : boardDBS) {
//            logger.debug("leaderboard id,{}", boardConfig.getId());
            Object o = null;
            LeaderboardBaseService leaderboardBaseService = getService(boardConfig.getEveryHours());
            if (leaderboardBaseService != null)
                o = leaderboardBaseService.findLatest(boardConfig.getId(), scoreDto.getPlayerId());

            /**
             * save score-data in global-board
             */
            if (!boardConfig.getProfileFieldName().isEmpty()) {
                globalScore = newScore(boardConfig.getCalculate(), (Integer) publicData.getOrDefault(boardConfig.getProfileFieldName(), 0), scoreDto.getScore());
                publicData.put(boardConfig.getProfileFieldName(), globalScore);
                profileDecorator.updatePublicData(publicData, scoreDto.getPlayerId());

            }
            if (boardConfig.getType().equals(LeaderBoardType.TOP)) {
//                logger.debug("is Top .... set score done");
                Global global = null;
                global = new Global(boardConfig.getGamePlatformLinkId(), boardConfig.getId(), scoreDto.getPlayerId(), globalScore, boardConfig.getTopCount());
                global.setExtraData(scoreDto.getExtraData());
                globalLeaderboardService.save(global);
            } else {

                if (isWeekly.test(boardConfig.getEveryHours()) && isStarted.test(boardConfig.getStartTime())) {
//                    logger.debug("is weekly .... set score done");
                    latestWeekly = (Weekly) o;
                    oldScore = (latestWeekly == null || latestWeekly.getExpireAt().isBefore(LocalDateTime.now())) ? 0
                            : latestWeekly.getTotalScore();

                    Integer newScore = newScore(boardConfig.getCalculate(), oldScore, scoreDto.getScore());
                    globalScore = newScore;
                    Weekly weekly = new Weekly(boardConfig.getId(), scoreDto.getPlayerId(), newScore,
                            scoreDto.getScore(), publicData.get("country").toString());
                    weekly.setExpireAt(WiniUtil.expireAt(boardConfig.getStartTime(), WiniUtil.toEpochSecond(), Double.valueOf(boardConfig.getEveryHours())));
                    weekly.setExtraData(scoreDto.getExtraData());
                    getService(boardConfig.getEveryHours()).save(weekly);
                    dayOrWeekOfYear = WiniUtil.weekOfYear();

                } else if (isDaily.test(boardConfig.getEveryHours()) && isStarted.test(boardConfig.getStartTime())) {
//                    logger.debug("is daily .... set score done");
                    latestDaily = Daily.class.cast(o);

                    oldScore = (latestDaily == null || latestDaily.getExpireAt().isBefore(LocalDateTime.now())) ? 0
                            : latestDaily.getTotalScore();
                    Integer newScore = newScore(boardConfig.getCalculate(), oldScore, scoreDto.getScore());
                    globalScore = newScore;
                    Daily daily = new Daily(boardConfig.getId(), scoreDto.getPlayerId(), newScore, scoreDto.getScore(), publicData.get("country").toString());
                    daily.setExpireAt(WiniUtil.expireAt(boardConfig.getStartTime(), WiniUtil.toEpochSecond(), Double.valueOf(boardConfig.getEveryHours())));
                    daily.setExtraData(scoreDto.getExtraData());
                    getService(boardConfig.getEveryHours()).save(daily);
                    dayOrWeekOfYear = WiniUtil.dayOfYear();

                } else if (isThreeDays.test(boardConfig.getEveryHours()) && isStarted.test(boardConfig.getStartTime())) {
//                    logger.debug("is threeDays .... set score done");
                    latestThreeDays = ThreeDays.class.cast(o);

                    oldScore = (latestThreeDays == null || latestThreeDays.getExpireAt().isBefore(LocalDateTime.now())) ? 0
                            : latestThreeDays.getTotalScore();
                    Integer newScore = newScore(boardConfig.getCalculate(), oldScore, scoreDto.getScore());
                    globalScore = newScore;
                    ThreeDays threeDays = new ThreeDays(boardConfig.getId(), scoreDto.getPlayerId(), newScore, scoreDto.getScore(), publicData.get("country").toString());
                    threeDays.setExpireAt(WiniUtil.expireAt(boardConfig.getStartTime(), WiniUtil.toEpochSecond(), Double.valueOf(boardConfig.getEveryHours())));
                    threeDays.setExtraData(scoreDto.getExtraData());
                    getService(boardConfig.getEveryHours()).save(threeDays);
                    dayOrWeekOfYear = WiniUtil.dayOfYear();

                } else if (isHour.test(boardConfig.getEveryHours()) && isStarted.test(boardConfig.getStartTime())) {
//                    logger.debug("is hourly .... set score done");
                    latestHour = Hour.class.cast(o);

                    oldScore = (latestHour == null || latestHour.getExpireAt().isBefore(LocalDateTime.now())) ? 0
                            : latestHour.getTotalScore();
                    Integer newScore = newScore(boardConfig.getCalculate(), oldScore, scoreDto.getScore());
                    globalScore = newScore;
                    Hour hour = new Hour(boardConfig.getId(), scoreDto.getPlayerId(), newScore, scoreDto.getScore(), publicData.get("country").toString());
                    hour.setExpireAt(WiniUtil.expireAt(boardConfig.getStartTime(), WiniUtil.toEpochSecond(), (double) (boardConfig.getEveryHours() / 100) / 60));
                    hour.setExtraData(scoreDto.getExtraData());
                    getService(boardConfig.getEveryHours()).save(hour);
                    dayOrWeekOfYear = WiniUtil.dayOfYear();
                }

            }

            /*
              Archive every set-score data in archive-board
             */


            ArchiveLeaderbord archiveLeaderbord = new ArchiveLeaderbord(boardConfig.getId(), scoreDto.getPlayerId(), globalScore,
                    scoreDto.getScore(), publicData.get("country").toString(), dayOrWeekOfYear);
            archiveLeaderbord.setExtraData(scoreDto.getExtraData());
            archiveLeaderboardService.save(archiveLeaderbord);
        }

    }


    @Override
    public List<LeaderBoardConfig> findByGamePlatformLinkId(String token) {
        Profile profile = profileService.findByToken(token);
        return leaderBoardConfigService.findByGamePlatformLinkId(profile.getGamePlatformLinkId());
    }


    @Override
    public List<RankData> entries(String leaderboardId, String token, Boolean includeCountry) {
        LeaderBoardConfig boardDB = leaderBoardConfigService.findOne(leaderboardId);
        if (boardDB.getType().equals(LeaderBoardType.TOP)) {
            return globalLeaderboardService.top(leaderboardId, boardDB.getReturnKeys());
        } else {
            String playerId = jwtTokenUtil.getProfileIdfromToken(token.substring(7));
            return aroundMe(leaderboardId, playerId, includeCountry);
        }
    }

    public LeaderboardBaseService getService(Integer everyHours) {

        return leaderboardBaseServices
                .stream()
                .filter(leaderboardBaseService -> leaderboardBaseService.getLeaderboardType() == everyHours ||
                        (everyHours >= 1000 && leaderboardBaseService.getLeaderboardType() >= everyHours))
                .findFirst().orElse(null);
    }


    private Integer newScore(String type, Integer oldScore, Integer newScore) {
        switch (LeaderBoardSubmitScoreType.valueOf(type)) {
            case MAX:
                newScore = Integer.max(oldScore, newScore);
                break; //TODO Karimi
            case LAST:
                newScore = newScore * 1;
                break; //TODO Karimi
            case SUM:
                newScore = Integer.sum(oldScore, newScore);
                break;
            case MIN: //TODO Karimi
                newScore = Integer.min(oldScore, newScore);
                break; //TODO Karimi
        }
        return newScore < 0 ? 0 : newScore;
    }


}
