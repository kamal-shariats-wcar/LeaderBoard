package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.config.security.JwtTokenUtil;
import com.wini.leader_board_integration.data.dto.leaderboard.RankData;
import com.wini.leader_board_integration.data.enums.LeaderBoardType;
import com.wini.leader_board_integration.data.model.GamePlatformLink;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.decorator.impl.LeaderboardDecoratorImpl;
import com.wini.leader_board_integration.repository.ProfileRepository;
import com.wini.leader_board_integration.service.GamePlatformLinkService;
import com.wini.leader_board_integration.service.LeaderBoardConfigService;
import com.wini.leader_board_integration.service.UserService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by kamal on 1/5/2019.
 */
@Service
public class ProfileServiceImpl implements ProfileService {
    private final Logger logger = LoggerFactory.getLogger(com.wini.leader_board_integration.service.impl.ProfileServiceImpl.class);
    private final ProfileRepository profileRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final CacheManager cacheManager;
    private final GamePlatformLinkService gamePlatformLinkService;
    private final UserService userService;
    MongoTemplate mongoTemplate;

    @Autowired
    private LeaderBoardConfigService leaderBoardConfigService;

    @Autowired
    private LeaderboardDecoratorImpl leaderboardDecorator;

    public ProfileServiceImpl(final ProfileRepository profileRepository, final JwtTokenUtil jwtTokenUtil, final CacheManager cacheManager, final GamePlatformLinkService gamePlatformLinkService, UserService userService) {
        this.profileRepository = profileRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.cacheManager = cacheManager;
        this.gamePlatformLinkService = gamePlatformLinkService;
        this.userService = userService;
    }

    @Override
    public Profile save(final Profile profile) {
        final Profile savedProfile = profileRepository.save(profile);
        if (savedProfile.getUserId() != null && !savedProfile.getUserId().isEmpty()) {
            final User user = userService.findOne(savedProfile.getUserId());
            savedProfile.getPublicData().put("username", user.getUsername());
        }
        return savedProfile;
    }

    @Override
    public Profile findOne(final String profileId) {
        final Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile != null) {
            if (profile.getUserId() != null && !profile.getUserId().isEmpty()) {
                final User user = userService.findOne(profile.getUserId());
                profile.getPublicData().put("username", user.getUsername());
            }
        }
        return profile;
    }

    @Override
    public Object getPublicData(final ObjectId profileId) {

        Profile profile = findOne(profileId.toString());

        if (profile != null) {
            Map<String, Object> publicData = profile.getPublicData();
            if (profile.getMutableData() != null) {
                Map<String, Object> mutableData = profile.getMutableData();
                publicData.put("followMe", mutableData.get("followMe"));
            }
//            if (publicData.get("countryImageUrl") != null) {
//                publicData.replace("countryImageUrl", (countryImagesPath + publicData.get("countryImageUrl").toString()));
//            }
//            GamePlatformLink gamePlatformLink = gamePlatformLinkService.findOne(profile.getGamePlatformLinkId().toString());
//            if (gamePlatformLink.getDefaultLeaderboard() != null) {
//
//                    publicData.put("rank", leaderBoardService.getEntryCount(new GetPlayerRank(profileId.toString(), gamePlatformLink.getDefaultLeaderboard(), false)).getResult());
//                }

            //TODO Karimi ---------------------
            GamePlatformLink gamePlatformLink = gamePlatformLinkService.findOne(profile.getGamePlatformLinkId());
            LeaderBoardConfig boardConfig = leaderBoardConfigService.findByGamePlatformLinkId(gamePlatformLink.getId())
                    .stream()
                    .filter(lead -> lead.getType().equals(LeaderBoardType.MASTER))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(boardConfig)) {

                RankData playerRank = leaderboardDecorator.getService(boardConfig.getEveryHours()).rankFor(boardConfig.getId(), profileId.toString(), boardConfig.getReturnKeys(), false);

                if (publicData.get("rank") != null) {
                    publicData.replace("rank", playerRank != null ? playerRank.getRank() : 0);
                } else {
                    publicData.put("rank", playerRank != null ? playerRank.getRank() : 0);
                }

            }
            //TODO Karimi ---------------------


            return publicData;
        }
        return null;
    }

    @Override
    public Map<String, Object> getPublicDataByUser(final String requestedProfileId, final String userProfileId) {
        Profile profile = findOne(requestedProfileId);
        Boolean blockedYou = Boolean.FALSE;
        if (profile != null) {
            Map<String, Object> publicData = profile.getPublicData();
            if (profile.getMutableData() != null) {
                Map<String, Object> mutableData = profile.getMutableData();
                publicData.put("followMe", mutableData.get("followMe"));
            }
            List<String> blockList = (List<String>) profile.getPrivateData().getOrDefault("blockList", new ArrayList<>());
            if (blockList.contains(userProfileId)) {
                blockedYou = Boolean.TRUE;
            }
            publicData.put("blockedYou", blockedYou.booleanValue());
//            if (publicData.get("countryImageUrl") != null) {
//                publicData.replace("countryImageUrl", (countryImagesPath + publicData.get("countryImageUrl").toString()));
//            }
//            GamePlatformLink gamePlatformLink = gamePlatformLinkService.findOne(profile.getGamePlatformLinkId());
//            if (gamePlatformLink.getDefaultLeaderboard() != null) {
//
//                publicData.put("rank", leaderBoardService.getEntryCount(new GetPlayerRank(requestedProfileId, gamePlatformLink.getDefaultLeaderboard(), false)).getResult());
//            }


            //TODO Karimi ---------------------
            GamePlatformLink gamePlatformLink = gamePlatformLinkService.findOne(profile.getGamePlatformLinkId());
            LeaderBoardConfig boardConfig = leaderBoardConfigService.findByGamePlatformLinkId(gamePlatformLink.getId())
                    .stream()
                    .filter(lead -> lead.getType().equals(LeaderBoardType.MASTER))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(boardConfig)) {

                RankData playerRank = leaderboardDecorator.getService(boardConfig.getEveryHours()).rankFor(boardConfig.getId(), requestedProfileId, boardConfig.getReturnKeys(), false);

                if (publicData.get("rank") != null) {
                    publicData.replace("rank", playerRank != null ? playerRank.getRank() : 0);
                } else {
                    publicData.put("rank", playerRank != null ? playerRank.getRank() : 0);
                }

            }
            //TODO Karimi ---------------------


            return publicData;
        }
        return new HashMap<>();
    }

    @Override
    public Profile findByLoginPlatformPlayerId(final String id) {
        final Profile profile = profileRepository.findProfileByLoginPlatformPlayerId(id);
        if (profile != null) {
            if (profile.getUserId() != null && !profile.getUserId().isEmpty()) {
                final User user = userService.findOne(profile.getUserId());
                profile.getPublicData().put("username", user.getUsername());
            }
        }
        return profile;
    }

    public Profile insertByKey(final String path, final Object value, final String profileId) {
        final Query query = new Query(Criteria.where("_id").is(profileId));
        mongoTemplate.updateFirst(query, Update.update(path, value), Profile.class);
        return findOne(profileId);
    }

    @Override
    public List<Profile> getAll() {
        return profileRepository.findAll();
    }

    @Override
    public Profile findByToken(final String token) {
        final String authToken = token.substring(7);
        return findOne(jwtTokenUtil.getProfileIdfromToken(authToken));
    }


    @Override
    public Profile findByFacebookId(final String facebookId) {
        final Profile profile = profileRepository.findByLoginPlatformDataLoginPlatformProfile_Id(facebookId);
        if (profile != null) {
            if (profile.getUserId() != null && !profile.getUserId().isEmpty()) {
                final User user = userService.findOne(profile.getUserId());
                profile.getPublicData().put("username", user.getUsername());
            }
        }
        return profile;
    }

    @Override
    public Profile findByPhoneNum(final String phoneNum) {
        final Profile profile = profileRepository.findByPrivateData_PhoneNum(phoneNum);
        if (profile != null) {
            if (profile.getUserId() != null && !profile.getUserId().isEmpty()) {
                final User user = userService.findOne(profile.getUserId());
                profile.getPublicData().put("username", user.getUsername());
            }
        }
        return profile;
    }

    @Override
    public Profile findByUserDeletionId(String id) {
        return profileRepository.findByPublicData_UserDeletionId(id);
    }

    @Override
    public Optional<Profile> findById(String profileId) {
        return profileRepository.findById(profileId);
    }
}
