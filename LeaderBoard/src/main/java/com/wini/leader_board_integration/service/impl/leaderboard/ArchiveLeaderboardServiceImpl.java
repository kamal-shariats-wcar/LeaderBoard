package com.wini.leader_board_integration.service.impl.leaderboard;


import com.wini.leader_board_integration.data.model.leaderboard.ArchiveLeaderbord;
import com.wini.leader_board_integration.repository.ArchiveLeaderboardRepository;
import com.wini.leader_board_integration.service.ArchiveLeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArchiveLeaderboardServiceImpl implements ArchiveLeaderboardService {


    private final ArchiveLeaderboardRepository archiveLeaderboardRepository;

    @Autowired
    public ArchiveLeaderboardServiceImpl(ArchiveLeaderboardRepository archiveLeaderboardRepository) {
        this.archiveLeaderboardRepository = archiveLeaderboardRepository;
    }

    @Override
    public void save(ArchiveLeaderbord archiveLeaderbord) {
        archiveLeaderboardRepository.save(archiveLeaderbord);
    }
}
