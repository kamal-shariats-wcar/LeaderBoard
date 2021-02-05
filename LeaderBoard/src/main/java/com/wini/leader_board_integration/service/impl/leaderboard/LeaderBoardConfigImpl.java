package com.wini.leader_board_integration.service.impl.leaderboard;

import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.repository.LeaderBoardRepository;
import com.wini.leader_board_integration.service.LeaderBoardConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaderBoardConfigImpl implements LeaderBoardConfigService {
    private final
    LeaderBoardRepository leaderBoardRepository;

    @Autowired
    public LeaderBoardConfigImpl(LeaderBoardRepository leaderBoardRepository) {
        this.leaderBoardRepository = leaderBoardRepository;
    }

    @Override
    public LeaderBoardConfig save(LeaderBoardConfig leaderBoardConfig) {
        return leaderBoardRepository.save(leaderBoardConfig);
    }

    @Override
    public void delete(String leaderboardId) {
        leaderBoardRepository.deleteById(leaderboardId);
    }

    @Override
    public LeaderBoardConfig findOne(String leaderBoardId) {
        Optional<LeaderBoardConfig> leaderBoardDBs = leaderBoardRepository.findById(leaderBoardId);
        return leaderBoardDBs.orElse(null);
    }

    @Override
    public List<LeaderBoardConfig> findAll() {
        return leaderBoardRepository.findAll();
    }

    @Override
    public List<LeaderBoardConfig> findByGamePlatformLinkId(String gamePlatformLinkId) {
        return leaderBoardRepository.findByGamePlatformLinkId(gamePlatformLinkId);
    }

    @Override
    public List<LeaderBoardConfig> findInList(List<String> leaderbordIds) {
        return leaderBoardRepository.findByIdIn(leaderbordIds);
    }
}
