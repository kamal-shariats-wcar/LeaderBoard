package com.wini.leader_board_integration.decorator;



import com.wini.leader_board_integration.data.info.LoginInfo;
import com.wini.leader_board_integration.data.info.PublicInfo;
import com.wini.leader_board_integration.data.vm.LoginVM;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by kamal on 1/2/2019.
 */
public interface LoginDecoratorService {
    LoginInfo login(HttpServletRequest request, LoginVM loginVM) throws IOException;

    PublicInfo getOutOfService();

    PublicInfo backToService();

    PublicInfo findProfileIdByFacebookId(String facebookId);


    PublicInfo userExist(String username);

    PublicInfo linkGuest(LoginVM loginVM, String token);

    LoginInfo generateToken(String profileId);
}
