package com.wini.leader_board_integration.repository;

import com.wini.leader_board_integration.data.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by kamal on 1/1/2019.
 */
public interface ProfileRepository extends MongoRepository<Profile, String> {
    //    @Query("{ 'loginPlatformPlayerId' : ?1 }")
    Profile findProfileByLoginPlatformPlayerId(String id);

//    Profile findProfileByUser_Username(String username);

    @Query(value = "{'loginPlatformData.loginPlatformProfile._id' : ?0}")
    Profile findByLoginPlatformDataLoginPlatformProfile_Id(String facebookId);


    @Query(value = "{'privateData.phoneNum' : ?0}")
    Profile findByPrivateData_PhoneNum(String phoneNum);

    @Query(value = "{'publicData.userDeletionId' : ?0}")
    Profile findByPublicData_UserDeletionId(String id);


}
