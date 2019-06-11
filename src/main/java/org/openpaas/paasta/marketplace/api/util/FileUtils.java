package org.openpaas.paasta.marketplace.api.util;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtils {

	public static String fileUpload(String path, MultipartFile partFile) throws Exception {
		String newFileName = "";

		File file = new File(path);
		if (file.exists() == false) {
			file.mkdirs();
		}
		log.info(partFile.getOriginalFilename() + ", " + partFile.getName() + ", " + partFile.getContentType() + ", " + partFile.getResource());

		newFileName = Long.toString(System.nanoTime()) + partFile.getContentType().replace("image/", "");
		file = new File(path + "/" + newFileName);
		partFile.transferTo(file);

		return newFileName;
	}

}
