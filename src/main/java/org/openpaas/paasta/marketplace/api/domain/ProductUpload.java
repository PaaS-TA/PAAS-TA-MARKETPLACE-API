package org.openpaas.paasta.marketplace.api.domain;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ProductUpload {

	private Long productId;

	@JsonIgnore
	private MultipartFile appIcon;

	@JsonIgnore
	private List<MultipartFile> screenshots;
	private List<Screenshot> screenshotsResult;

	@JsonIgnore
	private List<MultipartFile> fileNames;

}
