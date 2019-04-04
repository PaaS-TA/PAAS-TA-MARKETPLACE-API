package org.openpaas.paasta.marketplace.api.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        log(request, body);

        ClientHttpResponse response = execution.execute(request, body);

        log(response);

        return response;
    }

    private void log(HttpRequest request, byte[] body) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("===========================request begin===========================");
            logger.debug("URI         : {}", request.getURI());
            logger.debug("Method      : {}", request.getMethod());
            logger.debug("Headers     : {}", request.getHeaders());
            logger.debug("Request body: {}", new String(body, "UTF-8"));
            logger.debug("===========================request end  ===========================");
        }
    }

    private void log(ClientHttpResponse response) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("===========================response begin===========================");
            logger.debug("Status code  : {}", response.getStatusCode());
            logger.debug("Status text  : {}", response.getStatusText());
            logger.debug("Headers      : {}", response.getHeaders());
            logger.debug("===========================response end  ===========================");
        }
    }

}
