package com.wini.leader_board_integration.controller;


import com.wini.leader_board_integration.data.enums.ErrorCodeEnum;
import com.wini.leader_board_integration.data.info.LoginInfo;
import com.wini.leader_board_integration.data.info.PublicInfo;
import com.wini.leader_board_integration.data.vm.LoginVM;
import com.wini.leader_board_integration.decorator.LoginDecoratorService;
import com.wini.leader_board_integration.decorator.ProfileDecorator;
import com.wini.leader_board_integration.exception.BusinessException;
import com.wini.leader_board_integration.util.RSAUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/")
public class LoginController {
    @Autowired
    LoginDecoratorService loginDecoratorService;

    @Autowired
    private ProfileDecorator profileDecorator;

    @Value("${service.version}")
    private String version;


    @Value("${winigames.mondia.private-key}")
    private String privateKey;

    @CrossOrigin(origins = "*")
    @PostMapping(value = "login", headers = "Accept=application/json")
    public ResponseEntity<?> login(final HttpServletRequest request, final @RequestBody LoginVM loginVM) throws IOException {
        LoginInfo loginInfo = loginDecoratorService.login(request, loginVM);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "userExist/{username}", headers = "Accept=application/json")
    public ResponseEntity<?> register(final HttpServletRequest request, final @PathVariable String username) throws IOException {
        PublicInfo publicInfo = loginDecoratorService.userExist(username);
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "is-alive", headers = "Accept=application/json")
    public ResponseEntity<?> isAlive(final HttpServletRequest request) {
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.getResult().put("isAlive", "OK");
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "api/v1/version", headers = "Accept=application/json")
    public ResponseEntity<Map<String, String>> serviceVersion(@RequestHeader(value = "Authorization") final String token) {
        Map<String, String> result = new HashMap<>();
        result.put("version", version);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "api/v1/find-profile/{facebookId}", headers = "Accept=application/json")
    public ResponseEntity<PublicInfo> findProfileId(@PathVariable(value = "facebookId") String facebookId,
                                                    @RequestHeader(value = "Authorization") final String token) {

        PublicInfo publicInfo = loginDecoratorService.findProfileIdByFacebookId(facebookId);
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "GetOutOfService", headers = "Accept=application/json")
    public ResponseEntity<?> getOutOfService(@RequestHeader(value = "Authorization") final String token) {
        PublicInfo publicInfo = loginDecoratorService.getOutOfService();
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "BackToService", headers = "Accept=application/json")
    public ResponseEntity<?> backToService(@RequestHeader(value = "Authorization") final String token) {
        PublicInfo publicInfo = loginDecoratorService.backToService();
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "link", headers = "Accept=application/json")
    public ResponseEntity<?> link(final @RequestBody LoginVM loginVM, @RequestHeader(value = "Authorization") final String token) {
        PublicInfo publicInfo = loginDecoratorService.linkGuest(loginVM, token);
        return new ResponseEntity<>(publicInfo, HttpStatus.OK);
    }


    @CrossOrigin(origins = "*")
    @PostMapping(value = "test", headers = "Accept=application/json")
    public ResponseEntity<?> test(final HttpServletRequest request, final @RequestBody String body) throws IOException {
        System.out.println("leaderBoard report : " + body);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "token/{profileId}", headers = "Accept=application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateToken(final HttpServletRequest request, @PathVariable final String profileId, @RequestHeader(value = "Authorization") final String token) {
        return new ResponseEntity<>(loginDecoratorService.generateToken(profileId), HttpStatus.OK);
    }


    @CrossOrigin(origins = "*")
    @PostMapping(value = "api/v1/login-by-link", headers = "Accept=application/json")
    public ResponseEntity<?> loginByLink(@RequestBody Map<String, Object> data) {

        String phoneNum = null;
        try {
            phoneNum = RSAUtils.decrypt(data.get("token").toString());
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException |
                NoSuchAlgorithmException | NoSuchPaddingException | IllegalArgumentException e) {
            throw BusinessException.wrap(ErrorCodeEnum.INVALID, "Invalid token");
        }
        return new ResponseEntity<>(profileDecorator.checkPhoneStatus(phoneNum), HttpStatus.OK);
    }

}
