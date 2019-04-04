package org.openpaas.paasta.marketplace.api.config;

import org.openpaas.paasta.marketplace.api.thirdparty.paas.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SpecifiedUserTokenService {

	private static final Logger logger = LoggerFactory.getLogger(SpecifiedUserTokenService.class);

//    @Resource(name = "commRest")
    RestTemplate commRest;

    public User login(String usename, String password) {
        User user = commRest.postForObject("/login?email={email}&password={password}", null, User.class, usename,
                password);
        logger.info("user: {}", user);

        return user;
    }

}
