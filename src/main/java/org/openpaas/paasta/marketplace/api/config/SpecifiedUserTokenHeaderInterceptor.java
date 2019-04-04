package org.openpaas.paasta.marketplace.api.config;

import java.io.IOException;

import org.openpaas.paasta.marketplace.api.thirdparty.paas.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class SpecifiedUserTokenHeaderInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(SpecifiedUserTokenHeaderInterceptor.class);

    private String username;

    private String password;

    private String authTokenHeaderName;

    private SpecifiedUserTokenService specifiedUserTokenService;

    public SpecifiedUserTokenHeaderInterceptor(String authTokenHeaderName, String username, String password) {
        logger.info("SpecifiedUserTokenHeaderInterceptor: init");

        this.authTokenHeaderName = authTokenHeaderName;
        this.username = username;
        this.password = password;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        User user = specifiedUserTokenService.login(username, password);

        logger.info("users.getToken(): {}", user.getToken());

        request.getHeaders().set(authTokenHeaderName, user.getToken());

        logger.info("request.getHeaders(): {}", request.getHeaders());

        return execution.execute(request, body);
    }

    public void setSpecifiedUserTokenService(SpecifiedUserTokenService specifiedUserTokenService) {
        this.specifiedUserTokenService = specifiedUserTokenService;
    }

}
