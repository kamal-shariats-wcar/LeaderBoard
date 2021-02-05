package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.data.enums.LoginPlatformType;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.Role;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.repository.RoleRepository;
import com.wini.leader_board_integration.repository.UserRepository;
import com.wini.leader_board_integration.service.SequenceService;
import com.wini.leader_board_integration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by kamal on 1/5/2019.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SequenceService sequenceService;
    private final PasswordEncoder passwordEncoder;
    public static long counter;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, SequenceService sequenceService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sequenceService = sequenceService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findOne(String userId) {
        return userRepository.findById(userId).orElse(null);

    }

    @Override
    public User findByProfileId(String profileId) {
//        ObjectId id = new ObjectId(profileId);
        return userRepository.findByProfileId(profileId);
    }

    @Override
    public Role getUserRole() {
        return roleRepository.findByRoleName("ROLE_USER");
    }

    @Override
    public User getUser(Profile profile, String firstName, LoginPlatformType loginPlatformType,final String ip) {
        User user = new User();
        user.setUsername(firstName + "_" + sequenceService.generateSequence(User.SEQUENCE_NAME));
        user.setProfileId(profile.getId());
        user.setAuthType(loginPlatformType.getValue());
//        user.setLastSendDate(new Date());
        user.setRoles(new HashSet<>(Collections.singleton(getUserRole())));
        user.setEnables(Boolean.TRUE);
        user.setFirstLoginIP(ip);
        user.setLatestLoginIP(ip);
        return save(user);
    }
    @Override
    public User getUserPlayer(Profile profile, Map<String, Object> playerRegisterData, LoginPlatformType loginPlatformType) {
        final User user = new User();
        user.setUsername(playerRegisterData.get("username").toString());
        user.setPassword(passwordEncoder.encode(playerRegisterData.get("password").toString()));
        if (playerRegisterData.get("email") != null) {
            user.setEmail(String.valueOf(playerRegisterData.get("email")));
        }
        user.setProfileId(profile.getId());
        user.setAuthType(loginPlatformType.getValue());
//        user.setLastSendDate(new Date());
        user.setRoles(new HashSet<>(Collections.singleton(getUserRole())));
        user.setEnables(Boolean.TRUE);
        return save(user);
    }
    @Override
    public Boolean existUser(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public List<User> findByRole(String role) {
        return userRepository.findAllByRoles_roleName(role);
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


}
