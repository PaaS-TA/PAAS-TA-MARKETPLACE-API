package org.openpaas.paasta.marketplace.api.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class SoftwareUpload {

	private Long softwareId;

	@JsonIgnore
	private List<MultipartFile> appIcons;

	@JsonIgnore
	private List<MultipartFile> screenshots;
	private List<Screenshot> screenshotsResult;

	@JsonIgnore
	private List<MultipartFile> userManuals;

	@JsonIgnore
	private List<MultipartFile> fileNames;

}
