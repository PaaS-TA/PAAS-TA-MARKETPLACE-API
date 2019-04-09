package org.openpaas.paasta.marketplace.api.service;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.openpaas.paasta.marketplace.api.common.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 로그인
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-01-09
 */
@Service
public class LoginService extends Common {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    ConcurrentHashMap<String, OAuth2AccessToken> tokenCaches = new ConcurrentHashMap<>();


    /**
     * CF 로그인
     *
     * @param id
     * @param password
     * @return OAuth2AccessToken
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public OAuth2AccessToken login(String id, String password) throws MalformedURLException, URISyntaxException {
        CloudCredentials cc = new CloudCredentials(id, password);
        OAuth2AccessToken token = new CloudFoundryClient(cc, getTargetURL(cfApiUrl), true).login();
        //tokenCaches.put(token.getValue(), token);
        return token;
    }
    public OAuth2AccessToken refresh(String token, String refreshToken) throws MalformedURLException, URISyntaxException {
        CloudCredentials cc = new CloudCredentials(getOAuth2Token(token, refreshToken), true);
        OAuth2AccessToken newToken = new CloudFoundryClient(cc, getTargetURL(cfApiUrl), true).login();

        return newToken;
    }

    private final OAuth2AccessToken getOAuth2Token(String token, String refreshToken) {
        DefaultOAuth2AccessToken oAuthToken = new DefaultOAuth2AccessToken( token );
        oAuthToken.setRefreshToken( new DefaultOAuth2RefreshToken( refreshToken ) );

        return oAuthToken;
    }
}
