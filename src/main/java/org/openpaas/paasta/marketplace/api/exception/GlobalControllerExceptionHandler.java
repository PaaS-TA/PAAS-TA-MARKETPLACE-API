package org.openpaas.paasta.marketplace.api.exception;

import javax.servlet.http.HttpServletResponse;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public void runtimeExceptionHandler(HttpServletResponse response, RuntimeException ex) throws Exception {
		log.info("runtimeExceptionHandler : " + ex);

		// 에러는 "FAIL"로 코드 통일
		String message =
				"{\"resultCode\":\"" + ApiConstants.RESULT_STATUS_FAIL + "\"" +
                ",\"resultMessage\":\"" + ex.getMessage() + "\"}";

        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(message);
        response.flushBuffer();
	}

}
