package com.wini.leader_board_integration.data.model.notification;

import com.wini.leader_board_integration.data.model.BaseDoc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document("notification")
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseDoc {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String name;
    private Map<String, Object> position;
    private Map<String, Object> type;
    private Map<String, Object> data;
    private Boolean state;
    private Long expDate;

    public void fromNotification(com.wini.leader_board_integration.data.model.notification.Notification notification) {
        if (notification.getData() != null) {
            this.data = notification.getData();
        }
        if (notification.getName() != null) {
            this.name = notification.getName();
        }
        if (notification.getPosition() != null) {
            this.position = notification.getPosition();
        }
        if (notification.getType() != null) {
            this.type = notification.getType();
        }
        if (notification.getState() != null) {
            this.state = notification.getState();
        }
        if (notification.getExpDate() != null) {
            this.expDate = notification.getExpDate();
        }
    }
}
