package com.wini.leader_board_integration.events;

import com.wini.leader_board_integration.data.model.Profile;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
public class NotificationEvent extends ApplicationEvent {
    private final Profile profile;
    private final   Map<String, String> data;
    private final   String templateId;

    public NotificationEvent(Object source, Profile profile, Map<String, String> data, String templateId) {
        super(source);
        this.profile = profile;
        this.data = data;
        this.templateId = templateId;
    }
}
