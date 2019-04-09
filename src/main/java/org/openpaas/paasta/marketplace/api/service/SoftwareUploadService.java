//package org.openpaas.paasta.marketplace.api.service;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import javax.transaction.Transactional;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FilenameUtils;
//import org.openpaas.paasta.marketplace.api.BaseComponent;
//import org.openpaas.paasta.marketplace.api.model.Screenshot;
//import org.openpaas.paasta.marketplace.api.model.Software;
//import org.openpaas.paasta.marketplace.api.model.SoftwareUpload;
//import org.openpaas.paasta.marketplace.api.repository.ScreenshotRepository;
//import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.util.Assert;
//import org.springframework.web.multipart.MultipartFile;
//
//@Service
//@Transactional
//public class SoftwareUploadService extends BaseComponent {
//
//	@Autowired
//	private SoftwareRepository softwareRepository;
//
//	@Autowired
//	private ScreenshotRepository screenshotRepository;
//
//	@Value("${local.uploadPath}")
//	private String LOCAL_UPLOADPATH;
//
//	public SoftwareUpload uploadSoftware(SoftwareUpload softwareUpload) {
//		Software software = softwareRepository.findById(softwareUpload.getSoftwareId()).orElse(null);
//
//		Assert.notNull(software, "The software must not be null");
//
//		String year = new SimpleDateFormat("yyyy").format(software.getCreatedDate());
//
//		logger.debug("year={}", year);
//
//		appIcons(softwareUpload);
//		softwareUpload.setScreenshotsResult(screenshots(softwareUpload, software));
//		userManuals(softwareUpload);
//		fileNames(softwareUpload);
//		return softwareUpload;
//	}
//
//	private void appIcons(SoftwareUpload softwareUpload) {
//		List<MultipartFile> multipartFiles = softwareUpload.getAppIcons();
//
//		logger.debug("appIcons={}", multipartFiles);
//
//		if (multipartFiles == null) {
//			return;
//		}
//
//		for (MultipartFile multipartFile : multipartFiles) {
//			String uploadPath = LOCAL_UPLOADPATH + "/software/" + softwareUpload.getSoftwareId() + "/appIcon";
//			transferTo(uploadPath, multipartFile);
//		}
//	}
//
//	private List<Screenshot> screenshots(SoftwareUpload softwareUpload, Software software) {
//		List<MultipartFile> multipartFiles = softwareUpload.getScreenshots();
//
//		logger.debug("screenshots={}", multipartFiles);
//
//		if (multipartFiles == null) {
//			return null;
//		}
//
//		List<Screenshot> screenshots = new ArrayList<>();
//
//		Long seq = 1L;
//
//		for (MultipartFile multipartFile : multipartFiles) {
//			Screenshot screenshot = new Screenshot();
//			screenshot.setFileName(multipartFile.getOriginalFilename());
//			screenshot.setSeq(seq++);
//			screenshots.add(screenshot);
//
//			String uploadPath = LOCAL_UPLOADPATH + "/screenshot";
//			transferTo(uploadPath, multipartFile);
//		}
//
//		List<Screenshot> screenshotList = screenshotRepository.saveAll(screenshots);
//
//		// software.setScreenshotList(screenshotList);
//		software.getScreenshotList().addAll(screenshotList);
//
//		return screenshotList;
//	}
//
//	private void userManuals(SoftwareUpload softwareUpload) {
//		List<MultipartFile> multipartFiles = softwareUpload.getUserManuals();
//
//		logger.debug("userManuals={}", multipartFiles);
//
//		if (multipartFiles == null) {
//			return;
//		}
//
//		for (MultipartFile multipartFile : multipartFiles) {
//			String uploadPath = LOCAL_UPLOADPATH + "/software/" + softwareUpload.getSoftwareId() + "/userManual";
//			transferTo(uploadPath, multipartFile);
//		}
//	}
//
//	private void fileNames(SoftwareUpload softwareUpload) {
//		List<MultipartFile> multipartFiles = softwareUpload.getFileNames();
//
//		logger.debug("fileNames={}", multipartFiles);
//
//		if (multipartFiles == null) {
//			return;
//		}
//
//		for (MultipartFile multipartFile : multipartFiles) {
//			String uploadPath = LOCAL_UPLOADPATH + "/software/" + softwareUpload.getSoftwareId() + "/fileName";
//			transferTo(uploadPath, multipartFile);
//		}
//	}
//
//	private void transferTo(String uploadPath, MultipartFile multipartFile) {
//		logger.debug("uploadPath={}", uploadPath);
//
//		String originalFilename = multipartFile.getOriginalFilename();
//		logger.debug("originalFilename={}", originalFilename);
//
//		boolean mkdirs = FileSystems.getDefault().getPath(uploadPath).toFile().mkdirs();
//		logger.debug("mkdirs={}", mkdirs);
//
//		String uuid = UUID.randomUUID().toString();
//		String extension = FilenameUtils.getExtension(originalFilename);
//		String more = uuid + "." + extension;
//		Path path = FileSystems.getDefault().getPath(uploadPath, more);
//		try {
//			multipartFile.transferTo(path);
//		} catch (IllegalStateException | IOException e) {
//			logger.error(e.getMessage());
//		}
//
//		File file = FileSystems.getDefault().getPath(uploadPath, more + ".txt").toFile();
//		final String data = originalFilename;
//		final Charset encoding = Charset.defaultCharset();
//		try {
//			FileUtils.writeStringToFile(file, data, encoding);
//		} catch (IOException e) {
//			logger.error(e.getMessage());
//		}
//	}
//
//}
