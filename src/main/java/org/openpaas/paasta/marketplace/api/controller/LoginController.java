package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 로그인
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-01-09
 */
@RestController
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    /**
     * CF 로그인
     *
     * @param body
     * @return Map
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    @PostMapping(value = "/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> body) throws MalformedURLException, URISyntaxException {
        String id = (String) body.get("id");
        String password = (String) body.get("password");

        Map<String, Object> result = new HashMap<>();
        OAuth2AccessToken token = loginService.login(id, password);

        result.put("user_id", token.getAdditionalInformation().get("user_id"));
        result.put("scope", token.getScope());
        result.put("token_type", token.getTokenType());
        result.put("token", token.getValue());
        result.put("refresh_token_type", token.getTokenType());
        result.put("refresh_token", token.getRefreshToken().getValue());
        result.put("expireDate", token.getExpiration().getTime() - 10000);
        result.put("expire_in", token.getExpiresIn());
        //USER_ID -- UAA 통일
        result.put("user_name", id);
        result.put("id", id);
        result.put("password", password);

        return result;

    }
}
