package com.wini.leader_board_integration.service.impl.leaderboard;

import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.service.LeaderBoardReport;
import com.wini.leader_board_integration.service.NotificationService;
import com.wini.leader_board_integration.service.impl.ProfileService;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

public class PartnersLeaderBoardReport extends LeaderBoardReport {


    public PartnersLeaderBoardReport(ProfileService profileService, LeaderboardHolder leaderboardHolder, MongoTemplate mongoTemplate, final NotificationService notificationService) {
        super(profileService, leaderboardHolder, mongoTemplate,notificationService);
    }

    @Override
    public void executeReportOperation(LeaderBoardConfig leaderBoardConfig) {
        final JSONObject reportConfigs = new JSONObject(leaderBoardConfig.getReportConfigs());
        final List<RankData> rankData = getLeaderBoardTops(leaderBoardConfig);
        final JSONArray reportUsers = new JSONArray();
        final JSONObject leaderBoardReportHistory = new JSONObject();
        UriComponentsBuilder report = UriComponentsBuilder.newInstance();
        rankData.forEach(r -> {
            final Profile profile = profileService.findOne(r.getId());
            final JSONObject user = new JSONObject();
            user.put("id", profile.getLoginPlatformPlayerId());
            user.put("rank", r.getRank());
            user.put("score", r.getScore());
            reportUsers.put(user);
        });
        report.queryParam("users", reportUsers);
        report.queryParam("game", reportConfigs.getString("game"));
        report.queryParam("leaderboard_type", reportConfigs.getString("leaderboad_type"));
        report.queryParam("leaderboard_serial", ObjectId.get().toString());
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/x-www-form-urlencoded");
        if (!reportConfigs.optString("reportAuthorization", "").isEmpty()) {
            httpHeaders.add("Authorization", reportConfigs.getString("reportAuthorization").trim());
        }
        final HttpEntity<String> httpEntity = new HttpEntity<>(report.toUriString().replace("?", ""), httpHeaders);
        final RestTemplate restTemplate = new RestTemplate();

        try {
            final URI uri = new URI(reportConfigs.getString("reportUrl"));
            final ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            leaderBoardReportHistory.put("responseFromPartner", response);
            leaderBoardReportHistory.put("reportToPartner", report);
        } catch (Exception e) {
            e.printStackTrace();
            leaderBoardReportHistory.put("responseFromPartner", "failed response");
            leaderBoardReportHistory.put("reportToPartner", report);
            leaderBoardReportHistory.put("reason", e.getMessage());
        }
        persistHistory(leaderBoardReportHistory,"partnersLeaderBoardReportHistory");
    }
}
