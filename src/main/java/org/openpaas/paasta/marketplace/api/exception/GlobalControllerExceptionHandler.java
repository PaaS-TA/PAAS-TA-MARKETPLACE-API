package org.openpaas.paasta.marketplace.api.exception;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public void runtimeExceptionHandler(HttpServletResponse response, RuntimeException ex) throws Exception {
		log.info("runtimeExceptionHandler");
		Gson gson = new Gson();
		Map<String, Object> bodyMap = new HashMap<String, Object>();
		// 에러는 "FAIL"로 코드 통일
		bodyMap.put("code", ApiConstants.RESULT_STATUS_FAIL);
		bodyMap.put("message", ex.getMessage());
		bodyMap.put("model", new Object());
//		String message =
//				"{\"code\":\"" + ApiConstants.RESULT_STATUS_FAIL + "\"" +
//                ",\"message\":\"" + ex.getMessage() + "\"" +
//                ",\"model\":\"" + gson.toJson(new Object()) + "\"}";

        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().print(gson.toJson(bodyMap));
        response.flushBuffer();
	}

}
