package com.wini.leader_board_integration.service.impl;

import com.wini.leader_board_integration.config.security.JwtTokenUtil;
import com.wini.leader_board_integration.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Created by kamal on 1/14/2019.
 */
@Service
public class SecurityServiceImpl implements SecurityService {
    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Override
    public String getUserToken(String userName) {
       final UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }

}
