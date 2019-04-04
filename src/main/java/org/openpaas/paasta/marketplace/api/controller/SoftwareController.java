package org.openpaas.paasta.marketplace.api.controller;

import java.util.List;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwareUpload;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.service.SoftwareUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/prototype/softwares")
public class SoftwareController extends AbstractController {

	@Autowired
    SoftwareService softwareService;

    @Autowired
    private SoftwareUploadService softwareUploadService;

    @GetMapping
    public List<Software> getSoftwareList(SoftwareSpecification spec) {
        logger.info("getSoftwareList: spec={}", spec);
 
        return softwareService.getSoftwareList(spec);
    }
    
    @GetMapping("/{id}")
    public Software getSoftware(@PathVariable("id") long id) {
        logger.info("getSoftware: id={}", id);
 
        return softwareService.getSoftware(id);
    }

    @PostMapping
    public Software createSoftware(@RequestBody Software software) {
        logger.info("createSoftware: software={}", software);
        return softwareService.createSoftware(software);
    }

    @PostMapping("/upload")
    public SoftwareUpload uploadSoftware(SoftwareUpload softwareUpload) {
        logger.info("uploadSoftware: softwareUpload={}", softwareUpload);
        return softwareUploadService.uploadSoftware(softwareUpload);
    }

}
