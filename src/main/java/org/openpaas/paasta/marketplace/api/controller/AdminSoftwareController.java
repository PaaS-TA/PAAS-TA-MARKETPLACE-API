package org.openpaas.paasta.marketplace.api.controller;

import javax.validation.constraints.NotNull;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/page")
    public Page<Software> getPage(SoftwareSpecification spec, Pageable pageable) {
        return softwareService.getPage(spec, pageable);
    }

    @GetMapping("/{id}")
    public Software get(@NotNull @PathVariable Long id) {
        return softwareService.get(id);
    }

    @PutMapping("/{id}")
    public Software update(@PathVariable @NotNull Long id,
            @NotNull @Validated(Software.Update.class) @RequestBody Software software, BindingResult bindingResult)
            throws BindException {
        Software sameName = softwareService.getByName(software.getName());
        if (sameName != null && id != sameName.getId()) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        software.setId(id);

        return softwareService.updateMetadata(software);
    }

}
