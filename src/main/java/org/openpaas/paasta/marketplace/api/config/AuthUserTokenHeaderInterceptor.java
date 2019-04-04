package org.openpaas.paasta.marketplace.api.config;

import java.io.IOException;

import org.openpaas.paasta.marketplace.api.thirdparty.paas.User;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class AuthUserTokenHeaderInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(AuthUserTokenHeaderInterceptor.class);

    private String authTokenHeaderName;

    public AuthUserTokenHeaderInterceptor(String authTokenHeaderName) {
        logger.info("AuthUserTokenHeaderInterceptor: init");

        this.authTokenHeaderName = authTokenHeaderName;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        User user = SecurityUtils.getUser();

        logger.info("user: {}", user);

        if (user != null) {
            request.getHeaders().set(authTokenHeaderName, user.getToken());
        }

        return execution.execute(request, body);
    }

}
