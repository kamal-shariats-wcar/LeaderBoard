package com.wini.leader_board_integration.data.dto;

import com.wini.leader_board_integration.data.model.Profile;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
public class ProfileDto {
    private String id;
    private Map<String,Object> publicData;
    private Map<String,Object> privateData;
    private Map<String,Object> mutableData;
    public ProfileDto(Profile profile) {
        if (profile.getId() != null) {
            this.id = profile.getId();
        }
        if (profile.getPublicData()!= null) {
                this.publicData = profile.getPublicData();
        }
        if (profile.getPrivateData() != null) {
                this.privateData = profile.getPrivateData();
        }
        if (profile.getMutableData() != null ) {
                this.mutableData =profile.getMutableData();
        }
    }
}
