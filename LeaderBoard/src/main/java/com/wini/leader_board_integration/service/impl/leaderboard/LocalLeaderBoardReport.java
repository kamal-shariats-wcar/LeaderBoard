package com.wini.leader_board_integration.service.impl.leaderboard;

import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.service.*;
import com.wini.leader_board_integration.service.impl.ProfileService;
import com.wini.leader_board_integration.util.WiniUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;

public class LocalLeaderBoardReport extends LeaderBoardReport {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalLeaderBoardReport.class);
    private final UserService userService;


    public LocalLeaderBoardReport(final ProfileService profileService, final LeaderboardHolder leaderboardHolder,
                                  final MongoTemplate mongoTemplate, final NotificationService notificationService, final UserService userService) {
        super(profileService, leaderboardHolder, mongoTemplate, notificationService);
        this.userService = userService;
    }


    @Override
    public void executeReportOperation(final LeaderBoardConfig leaderBoardConfig) {
        final List<RankData> rankData = getLeaderBoardTops(leaderBoardConfig);
        final Map<String, Object> defaultReward = (Map<String, Object>) leaderBoardConfig.getConfigs().getOrDefault("defaultReward", new HashMap<>());
        final ArrayList<Map<String, Object>> campaignRewards = (ArrayList<Map<String, Object>>) leaderBoardConfig.getConfigs().getOrDefault("campaignRewards", new ArrayList<>());
        ArrayList rewardLeaderboard = null;
        if (!defaultReward.isEmpty() && defaultReward.containsKey("rewardLeaderboard") && defaultReward.get("rewardLeaderboard") instanceof ArrayList) {
            rewardLeaderboard = (ArrayList) defaultReward.get("rewardLeaderboard");
        }

        final JSONArray finalDefaultRewardLeaderboard = new JSONArray(rewardLeaderboard);
        rankData.forEach(r -> {
            final Boolean[] receivedCampaign = {Boolean.FALSE};
            final Profile profile = profileService.findOne(r.getId());
            if (!campaignRewards.isEmpty()) {
                campaignRewards.forEach(cr -> {
                    final JSONObject campaignReward = new JSONObject(cr);
                    if (campaignReward.getBoolean("active")) {
                        final JSONArray campaignRewardLeaderboards = campaignReward.getJSONArray("rewardLeaderboard");
                        final JSONObject campaignRewardLeaderboard = campaignRewardLeaderboards.optJSONObject(r.getRank() - 1);
                        if (campaignRewardLeaderboard != null && !campaignRewardLeaderboard.isEmpty()) {
                            receivedCampaign[0]=(prizeCampaignReward(profile, campaignReward, campaignRewardLeaderboard, leaderBoardConfig, r));
                        }
                    }

                });
            }
            if (!receivedCampaign[0]) {
                final JSONObject reward = finalDefaultRewardLeaderboard.optJSONObject(r.getRank() - 1);
                if (reward != null && reward.get("fieldType").toString().trim().equalsIgnoreCase("publicData")) {
                    LOGGER.debug("publicData gift from defaultReward to player {}", profile.getId());
                    rewardPublicData(profile, reward, (String) defaultReward.get("rewardChatMessage"), new JSONObject(defaultReward), r.getRank());
                    final JSONObject leaderBoardReportHistory = new JSONObject();
                    leaderBoardReportHistory.put("type", "defaultReward");
                    leaderBoardReportHistory.put("profileId", profile.getId());
                    leaderBoardReportHistory.put("time", System.currentTimeMillis());
                    leaderBoardReportHistory.put("rank", r);
                    leaderBoardReportHistory.put("leaderBoardConfig", leaderBoardConfig);
                    persistHistory(leaderBoardReportHistory, "defaultRewardLeaderBoardHistory");
                }
            }
        });

    }

    private boolean rewardPublicData(final Profile profile, final JSONObject reward, String message, JSONObject includeNotificationConfig, final Integer rank) {
        final String fieldName = reward.get("fieldName").toString();
        if (profile.getPublicData().containsKey(fieldName)) {
            Long fieldValue;
            final String field = profile.getPublicData().get(fieldName).toString();
            if (field.contains(".")) {
                fieldValue = Long.valueOf(field.split("\\.")[0]);
            } else {
                fieldValue = Long.valueOf(field);
            }
            Long rewardValue;
            if (reward.get("amount").toString().contains(".")) {
                rewardValue = Long.valueOf(reward.get("amount").toString().split("\\.")[0]);
            } else {
                rewardValue = Long.valueOf(reward.get("amount").toString());
            }
            profile.getPublicData().put(fieldName, fieldValue + rewardValue);
            profileService.save(profile);
            message = message.replace(";publicData.name;", profile.getPublicData().getOrDefault("name", "player").toString());
            message = message.replace(";win.amount;", rewardValue.toString());
            message = message.replace(";win.fieldName;", fieldName);
            message = message.replace(";win.rank;", rank.toString());
            try {
                sendNotification(profile, includeNotificationConfig, reward, rank);
            } catch (Exception e) {
                LOGGER.warn("cant send leaderboard reward notification profileId:  {}", profile.getId());
            }
            return true;
        }
        return false;
    }

    private boolean prizeCampaignReward(final Profile profile, final JSONObject campaignReward, final JSONObject reward, final LeaderBoardConfig leaderBoardConfig, final RankData rankData) {
        final Map<String, Object> mutableData = profile.getMutableData();
        Boolean eligible = Boolean.FALSE;
        if (mutableData.containsKey("campaignsSetting") ) {
            final HashMap<String, Object> campaignSetting = (HashMap<String, Object>) mutableData.get("campaignsSetting");
            if (campaignSetting.containsKey("myCampaigns") && campaignSetting.get("myCampaigns") instanceof ArrayList) {
                final JSONArray myCampaigns = new JSONArray((ArrayList) campaignSetting.get("myCampaigns"));
                if (!myCampaigns.isEmpty()) {
                    final String campaignType = campaignReward.optString("campaignType", "NaN");
                    final String deepLinkId = campaignReward.optString("deepLinkId", "NaN");
                    if (WiniUtil.stringValueExist(myCampaigns, campaignType) && WiniUtil.stringValueExist(myCampaigns, deepLinkId)) {
                        eligible = Boolean.TRUE;
                        if (reward != null && reward.get("fieldType").toString().trim().equalsIgnoreCase("publicData")) {
                            LOGGER.debug("reward publicData from campaign to player {} from campaign",profile.getId());
                            rewardPublicData(profile, reward, campaignReward.getString("rewardChatMessage"), campaignReward, rankData.getRank());
                        } else {
                            LOGGER.debug("reward other gift from campaign to player {}",profile.getId());
                            String message = campaignReward.getString("rewardChatMessage");
                            message = message.replace(";publicData.name;", profile.getPublicData().getOrDefault("name", "player").toString());
                            message = message.replace(";win.amount;", String.valueOf(reward.getLong("amount")));
                            message = message.replace(";win.fieldName;", reward.getString("fieldName"));
                            message = message.replace(";win.rank;", rankData.getRank().toString());

                            sendNotification(profile, campaignReward, reward, rankData.getRank());
                        }
                    }
                    final JSONObject leaderBoardReportHistory = new JSONObject();
                    leaderBoardReportHistory.put("type", "campaignReward");
                    leaderBoardReportHistory.put("profileId", profile.getId());
                    leaderBoardReportHistory.put("time", System.currentTimeMillis());
                    leaderBoardReportHistory.put("rank", rankData);
                    leaderBoardReportHistory.put("eligible", eligible);
                    leaderBoardReportHistory.put("leaderBoardConfig", leaderBoardConfig);
                    persistHistory(leaderBoardReportHistory, "campaignRewardLeaderBoardHistory");
                }
            }
        }

        return eligible;

    }

    private void sendNotification(final Profile profile, final JSONObject containsNotificationConfig, final JSONObject rewardLeaderBoard, final Integer rank) {
        final User user = userService.findOne(profile.getUserId());
        if (user.getAuthType().equals(LoginPlatformType.FBInstant.getValue())) {
            final JSONObject fbInstantNotification = containsNotificationConfig.getJSONObject("fbInstantNotification");
            final Map<String, String> data = new LinkedHashMap<>();
            data.put("publicData.name", profile.getPublicData().getOrDefault("name", "player").toString());
            data.put("win.amount", rewardLeaderBoard.getString("amounts"));
            data.put("win.fieldName", rewardLeaderBoard.getString("fieldName"));
            data.put("win.rank", rank.toString());

            notificationService.fbInstancePushNotif(profile.getId(), fbInstantNotification.getString("templateId"), data);
        }
    }
}
