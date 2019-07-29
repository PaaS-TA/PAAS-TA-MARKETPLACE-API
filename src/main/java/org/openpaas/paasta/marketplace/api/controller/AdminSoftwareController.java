package org.openpaas.paasta.marketplace.api.controller;

import javax.validation.constraints.NotNull;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/admin/softwares")
@RequiredArgsConstructor
public class AdminSoftwareController {

    private final SoftwareService softwareService;

    @PutMapping("/{id}/status")
    public Software updateStatus(@PathVariable @NotNull Long id,
            @NotNull @Validated(Software.UpdateStatus.class) @RequestBody Software software) {
        software.setId(id);

        return softwareService.updateStatus(software);
    }

}
