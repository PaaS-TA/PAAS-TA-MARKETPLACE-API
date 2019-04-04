package org.openpaas.paasta.marketplace.api.config;

import java.util.Date;

import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.openpaas.paasta.marketplace.api.thirdparty.paas.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class AuthorizationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationUserDetailsService.class);

    private RestTemplate authRest;

    private UserRepository userRepository;

    public void setAuthRest(RestTemplate authRest) {
        this.authRest = authRest;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authToken) throws UsernameNotFoundException {
        Object credentials = authToken.getCredentials();
        logger.info("credentials: {}", credentials);

        if (credentials == null) {
            throw new AuthenticationCredentialsNotFoundException("authToken=" + authToken);
        }
        String token = (String) authToken.getCredentials();
        if (!StringUtils.hasText(token)) {
            throw new AuthenticationCredentialsNotFoundException("authToken=" + authToken);
        }

        User user = null;

        try {
            user = authRest.getForObject("/auth?token={token}", User.class, token);
            logger.info("user: {}", user);

            if (user != null && user.getId() != null && user.getToken() != null) {
                User loginUser = userRepository.findById(user.getId()).orElse(null);
                if (loginUser == null) {
                    loginUser = user;
                } else {
                    loginUser.setName(user.getName());
                    loginUser.setRole(user.getRole());
                    loginUser.setUpdatedDate(new Date());
                }
                userRepository.save(loginUser);
            }
        } catch (RestClientException e) {
            // ignore
            logger.error(e.getMessage());
        }

        if (user == null) {
            throw new UsernameNotFoundException("Not found user from authToken: " + authToken);
        }

        return user;
    }

}
