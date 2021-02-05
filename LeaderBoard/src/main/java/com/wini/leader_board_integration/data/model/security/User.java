package com.wini.leader_board_integration.data.model.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wini.leader_board_integration.data.model.BaseDoc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

/**
 * Created by kamal on 1/3/2019.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
public class User extends BaseDoc {
    private static final long serialVersionUID = 1L;
    @Transient
    public static final String SEQUENCE_NAME = "guest";
    //@JsonIgnore
    @Id
    private String userId;
    private String username;
    @JsonIgnore
    private String password;
    private String profileId;
    private Integer authType;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enables;
    private Date lastLogin;
    private Set<Role> roles;
    private String firstLoginIP;
    private String latestLoginIP;

}
