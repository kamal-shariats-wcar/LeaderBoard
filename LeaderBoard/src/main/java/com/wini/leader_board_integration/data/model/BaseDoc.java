package com.wini.leader_board_integration.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

import java.io.Serializable;

/**
 * Created by kamal on 1/1/2019.
 */
@Data
@NoArgsConstructor
public class BaseDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    @CreatedDate
    private Long createdAt;
    @JsonIgnore
    @LastModifiedDate
    private Long updatedAt;
    @Version
    @JsonIgnore
    private Integer version;
}
