package org.openpaas.paasta.marketplace.api.util;

import java.util.HashMap;
import java.util.Map;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;

public class ResponseUtils {

	public static Map<String, Object> apiResponse(Object obj) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			response.put("code", ApiConstants.RESULT_STATUS_SUCCESS);
			response.put("message", "");
			response.put("model", obj);
		} catch(Exception e) {
			response.put("code", ApiConstants.RESULT_STATUS_FAIL);
			response.put("message", e.getMessage());
			response.put("model", new Object());
		}

		return response;
	}

}
