package org.openpaas.paasta.marketplace.api.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerInterceptor extends HandlerInterceptorAdapter implements ClientHttpRequestInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("================================= START =================================");
		log.info("Request URI:\t" + request.getRequestURI());
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("================================== END ==================================");
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        log(request, body);

        ClientHttpResponse response = execution.execute(request, body);

        log(response);

        return response;
    }

    private void log(HttpRequest request, byte[] body) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("===========================request begin===========================");
            log.debug("URI         : {}", request.getURI());
            log.debug("Method      : {}", request.getMethod());
            log.debug("Headers     : {}", request.getHeaders());
            log.debug("Request body: {}", new String(body, "UTF-8"));
            log.debug("===========================request end  ===========================");
        }
    }

    private void log(ClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("===========================response begin===========================");
            log.debug("Status code  : {}", response.getStatusCode());
            log.debug("Status text  : {}", response.getStatusText());
            log.debug("Headers      : {}", response.getHeaders());
            log.debug("===========================response end  ===========================");
        }
    }

}
