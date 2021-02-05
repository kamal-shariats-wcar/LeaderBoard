package com.wini.leader_board_integration.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "friends")
public class Friends extends BaseDoc {
    @Id
    private String id;
    @DBRef
    private List<Profile> gameFriendsList;
    private String profileId;
}
