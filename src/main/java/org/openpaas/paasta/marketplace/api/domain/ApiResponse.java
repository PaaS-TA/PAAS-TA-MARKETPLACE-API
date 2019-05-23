package org.openpaas.paasta.marketplace.api.domain;

import lombok.Data;

@Data
public class ApiResponse {

	private String resultCode;
	private String resultMessage;
	private Object object;

}
