package com.wini.leader_board_integration.util;


import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.events.LeaderBoardReportEvent;
import com.wini.leader_board_integration.service.LeaderBoardConfigService;
import io.reactivex.rxjava3.core.Single;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Component
public class LeaderBoardReportInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(com.wini.leader_board_integration.util.LeaderBoardReportInit.class);
    private final LeaderBoardConfigService leaderBoardConfigService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public LeaderBoardReportInit(LeaderBoardConfigService leaderBoardConfigService, ApplicationEventPublisher applicationEventPublisher) {
        this.leaderBoardConfigService = leaderBoardConfigService;
        this.applicationEventPublisher = applicationEventPublisher;
        leaderBoardConfigService.findAll().forEach(cfg -> {
            if (cfg.getActive() && cfg.getReportConfigs() != null) {
                LOGGER.info("leaderBoard eligible for create report scheduler name {}", cfg.getName());
                final JSONObject reportConfigs = new JSONObject(cfg.getReportConfigs());
                if (reportConfigs.optBoolean("hasReport", false) &&
                        !reportConfigs.optString("reportUrl", "").isEmpty() &&
//                        !reportConfigs.optString("reportAuthorization","").isEmpty()&&
                        !reportConfigs.optString("game", "").isEmpty() &&
                        !reportConfigs.optString("leaderboad_type", "").isEmpty()) {
                    if (cfg.getEveryHours() >= 1000) {
                        final Double remainingMinute = (double) (cfg.getEveryHours() / 100);
                        final long currentEpoch = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis() / 1000;
                        final int seconds =WiniUtil.expireAt(cfg.getStartTime(), currentEpoch, remainingMinute/60).getSecond();
                        LOGGER.debug("start scheduler for leaderboard for {} after {} seconds",cfg.getName(),(seconds-5));
                        Single.create(emitter -> {
                            sendReportSeconds(cfg);
                        }).delaySubscription((seconds-5), TimeUnit.SECONDS).subscribe();
                    }
                    Single.create(emitter -> {
                        sendReport(cfg);
                    }).delaySubscription(remainingSeconds(cfg), TimeUnit.SECONDS).subscribe();
                }
            }
        });

    }

    public void report(final LeaderBoardConfig leaderBoardConfig) {
        Single.create(emitter -> {
            sendReport(leaderBoardConfig);
        }).delaySubscription(leaderBoardConfig.getEveryHours(), TimeUnit.HOURS).subscribe();
    }
    public void reportSeconds(final LeaderBoardConfig leaderBoardConfig) {
        final int remainingMinute = leaderBoardConfig.getEveryHours() / 100;
        LOGGER.debug("start scheduler for leaderboard for {} after {} minutes",leaderBoardConfig.getName(),remainingMinute);

        Single.create(emitter -> {
            sendReportSeconds(leaderBoardConfig);
        }).delaySubscription(remainingMinute, TimeUnit.MINUTES).subscribe();
    }
    public void sendReportSeconds(final LeaderBoardConfig leaderBoardConfig) {
        final LeaderBoardReportEvent leaderBoardReportEvent = new LeaderBoardReportEvent(this.getClass(), leaderBoardConfig);
        applicationEventPublisher.publishEvent(leaderBoardReportEvent);
        reportSeconds(leaderBoardConfig);
    }
    public void sendReport(final LeaderBoardConfig leaderBoardConfig) {
        final LeaderBoardReportEvent leaderBoardReportEvent = new LeaderBoardReportEvent(this.getClass(), leaderBoardConfig);
        applicationEventPublisher.publishEvent(leaderBoardReportEvent);
        report(leaderBoardConfig);
    }

    public long remainingSeconds(final LeaderBoardConfig leaderBoardConfig) {
        final long currentEpoch = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis() / 1000;
        final long expireAt = WiniUtil.expireAtEpoch(leaderBoardConfig.getStartTime(), currentEpoch, leaderBoardConfig.getEveryHours());
        final long expire = expireAt - currentEpoch;
        LOGGER.info("remaining Seconds for start report leaderBoard {} is  : {}", leaderBoardConfig.getName(), (expire - 10));
        return expire - 10;
    }
}
