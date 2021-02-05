package com.wini.leader_board_integration.service;



import com.wini.leader_board_integration.data.dto.Response;
import com.wini.leader_board_integration.data.model.notification.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    Notification save(Notification notification);

    Notification findOne(String id);

    void deleteById(String id);

    List<Notification> findAll();

    void deleteAll();

    List<Notification> findAllByStateAndExpDate(Boolean state,Long expDate);

    List<Notification> findActiveNotificationsExceptDisplayed(List<String> displayedIds);

    Response fbInstancePushNotif(String profileId, String templateId, Map<String, String> data);

    public Response fbInstancePushNotifByPlayerId(String playerId, String templateId, Map<String, String> data);
}
