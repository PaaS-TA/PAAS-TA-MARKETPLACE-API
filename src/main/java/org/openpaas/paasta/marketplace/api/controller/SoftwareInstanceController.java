package org.openpaas.paasta.marketplace.api.controller;

import java.util.List;

import org.openpaas.paasta.marketplace.api.model.SoftwareInstance;
import org.openpaas.paasta.marketplace.api.model.SoftwareInstanceSpecification;
import org.openpaas.paasta.marketplace.api.service.SoftwareInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/prototype/instances")
public class SoftwareInstanceController extends AbstractController {

	@Autowired
    SoftwareInstanceService softwareInstanceService;

    @GetMapping
    public List<SoftwareInstance> getSoftwareInstanceList(SoftwareInstanceSpecification spec) {
        logger.info("getSoftwareList: spec={}", spec);

        return softwareInstanceService.getSoftwareInstanceList(spec);
    }

    @GetMapping("/{id}")
    public SoftwareInstance getSoftwareInstance(@PathVariable("id") Long id) {
        logger.info("getSoftwareInstance: id={}", id);

        return softwareInstanceService.getSoftwareInstance(id);
    }

    @PostMapping
    public SoftwareInstance createSoftwareInstance(@RequestBody SoftwareInstance softwareInstance) {
        logger.info("createSoftwareInstance: softwareInstance={}", softwareInstance);

        return softwareInstanceService.createSoftwareInstance(softwareInstance);
    }

    @PutMapping("/{id}/provision")
    public SoftwareInstance provisionSoftwareInstance(@PathVariable("id") Long id) {
        logger.info("provisionSoftwareInstance: id={}", id);
        
        return softwareInstanceService.provision(id);
    }

}
