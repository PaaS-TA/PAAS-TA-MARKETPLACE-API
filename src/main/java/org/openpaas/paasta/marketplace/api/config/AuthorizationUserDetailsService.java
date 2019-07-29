package org.openpaas.paasta.marketplace.api.config;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.User;
import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Transactional
@Slf4j
public class AuthorizationUserDetailsService
        implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private RestTemplate authRest;

    private UserRepository userRepository;

    public void setAuthRest(RestTemplate authRest) {
        this.authRest = authRest;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public synchronized UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authToken)
            throws UsernameNotFoundException {
        Object credentials = authToken.getCredentials();
        log.info("credentials: {}", credentials);
        log.info("authRest: {}", authRest);

        if (credentials == null) {
            throw new AuthenticationCredentialsNotFoundException("token=" + authToken);
        }
        String token = (String) authToken.getCredentials();
        if (!StringUtils.hasText(token)) {
            throw new AuthenticationCredentialsNotFoundException("token=" + authToken);
        }

        User user = null;

        try {
            user = getUserByToken(token);

            log.info("user: {}", user);

            if (user != null && user.getId() != null) {
                log.info("userRepository: {}", userRepository);

                User loginUser = userRepository.findById(user.getId()).orElse(user);

                loginUser.setName(user.getName());
                loginUser.setRole(user.getRole());

                userRepository.save(loginUser);
            }
        } catch (RestClientException e) {
            // ignore
            log.error(e.getMessage());
        }

        if (user == null) {
            throw new UsernameNotFoundException("Not found user from authToken: " + authToken);
        }

        return user;
    }

    // FIXME: Dummy implementation
    private User getUserByToken(String token) {
        log.info("token={}", token);

        String id = StringUtils.trimAllWhitespace(token);
        if (!StringUtils.hasText(id)) {
            throw new IllegalStateException("Invalid token: value=" + token);
        }

        User user = new User();
        user.setId(id);
        user.setName("name-" + id);
        user.setRole(User.Role.User);
        if ("admin".equals(id)) {
            user.setRole(User.Role.Admin);
        }

        log.info("user={}", user);

        return user;
    }

}
