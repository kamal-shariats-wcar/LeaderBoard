package com.wini.leader_board_integration.controller.leaderboard;



import com.wini.leader_board_integration.data.dto.leaderboard.ScoreDto;
import com.wini.leader_board_integration.data.info.PlayerEntryInfo;
import com.wini.leader_board_integration.data.info.PublicInfo;
import com.wini.leader_board_integration.data.model.LeaderBoardConfig;
import com.wini.leader_board_integration.decorator.LeaderboardDecorator;
import com.wini.leader_board_integration.service.LeaderBoardConfigService;
import com.wini.leader_board_integration.service.LeaderBoardScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/v1/leaderboards")
@CrossOrigin("*")
public class LeaderBoardController {


    @Autowired
    LeaderBoardConfigService leaderBoardConfigService;
    @Autowired
    LeaderBoardScoreService leaderBoardScoreService;


    @Autowired
    private LeaderboardDecorator leaderboardDecorator;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "", headers = "Accept=application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PublicInfo> addLeaderBoard(@RequestBody LeaderBoardConfig leaderBoardConfig,
                                                     @RequestHeader(value = "Authorization") final String token) {
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.getResult().put("leaderboard", leaderboardDecorator.save(leaderBoardConfig));
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "", headers = "Accept=application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<LeaderBoardConfig> updateLeaderBoard(@RequestBody LeaderBoardConfig leaderBoardConfig,
                                                               @RequestHeader(value = "Authorization") final String token) {


        return new ResponseEntity<>(leaderboardDecorator.save(leaderBoardConfig), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/{leaderboardId}", headers = "Accept=application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PublicInfo> deleteLeaderBoard(@PathVariable(value = "leaderboardId") String leaderboardId,
                                                        @RequestHeader(value = "Authorization") final String token) {
        leaderboardDecorator.delete(leaderboardId);
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.getResult().put("deleted", true);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping(value = "")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PublicInfo> findByGamePlatformLinkId(@RequestHeader(value = "Authorization") final String token) {
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.getResult().put("leaderboards", leaderboardDecorator.findByGamePlatformLinkId(token));
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping(value = "/entries/{leaderboardId}/{includeCountry}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PublicInfo> getEntries(@PathVariable(value = "leaderboardId") String leaderboardId,
                                                 @PathVariable(value = "includeCountry") Boolean includeCountry,
                                                 @RequestHeader(value = "Authorization") final String token) {
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.getResult().put("entries", leaderboardDecorator.entries(leaderboardId, token, includeCountry));
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping(value = "/set-score", headers = "Accept=application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PublicInfo> setScore(@RequestBody final ScoreDto scoreDto,
                                               @RequestHeader(value = "Authorization") final String token) {
        leaderboardDecorator.setScore(scoreDto);
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.getResult().put("done", true);
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add-score/{leaderBoardId}", headers = "Accept=application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PlayerEntryInfo> addScore(HttpServletRequest request, @RequestParam(value = "playerId") String playerId,
                                                    @PathVariable String leaderBoardId, @RequestBody ScoreDto scoreDto,
                                                    @RequestHeader(value = "Authorization") final String token) {
        return null;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{leaderBoardId}", headers = "Accept=application/json")
    @CrossOrigin(origins = "*")
    public ResponseEntity<PublicInfo> getLeaderboard(@PathVariable(value = "leaderBoardId") String leaderBoardId,
                                                     @RequestHeader(value = "Authorization") final String token) {
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.getResult().put("leaderboard", leaderBoardConfigService.findOne(leaderBoardId));
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }

}
