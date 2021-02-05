package com.wini.leader_board_integration.data.vm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Created by kamal on 1/2/2019.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginPlatformProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    @Field(value = "_id")
    private  String id;
    private  String name;
    private  String pictureUrl;
}
