package org.openpaas.paasta.marketplace.api.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistory;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistorySpecification;
import org.openpaas.paasta.marketplace.api.domain.Yn;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/softwares")
@RequiredArgsConstructor
public class SoftwareController {

    private final SoftwareService softwareService;

    @GetMapping("/page")
    public Page<Software> getPage(SoftwareSpecification spec, Pageable pageable) {
        spec.setStatus(Status.Approval);
        spec.setInUse(Yn.Y);

        return softwareService.getPage(spec, pageable);
    }

    @GetMapping("/my/page")
    public Page<Software> getMyPage(SoftwareSpecification spec, Pageable pageable) {
        spec.setCreatedBy(SecurityUtils.getUserId());

        return softwareService.getPage(spec, pageable);
    }

    @GetMapping("/{id}")
    public Software get(@NotNull @PathVariable Long id) {
        return softwareService.get(id);
    }

    @PostMapping
    public Software create(@NotNull @Validated(Software.Create.class) @RequestBody Software software,
            BindingResult bindingResult) throws BindException {
        Software sameName = softwareService.getByName(software.getName());
        if (sameName != null) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return softwareService.create(software);
    }

    @PutMapping("/{id}")
    public Software update(@PathVariable @NotNull Long id,
            @NotNull @Validated(Software.Update.class) @RequestBody Software software, BindingResult bindingResult)
            throws BindException {
        Software saved = softwareService.get(id);
        SecurityUtils.assertCreator(saved);

        Software sameName = softwareService.getByName(software.getName());
        if (sameName != null && id != sameName.getId()) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        software.setId(id);

        return softwareService.update(software);
    }

    @GetMapping("/{id}/histories")
    public List<SoftwareHistory> getHistoryList(@NotNull @PathVariable Long id, Sort sort) {
        Software software = softwareService.get(id);
        SecurityUtils.assertCreator(software);

        SoftwareHistorySpecification spec = new SoftwareHistorySpecification();
        spec.setSoftwareId(id);

        return softwareService.getHistoryList(spec, sort);
    }

}
