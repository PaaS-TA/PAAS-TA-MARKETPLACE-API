package org.openpaas.paasta.marketplace.api.controller;

import javax.validation.constraints.NotNull;

import org.openpaas.paasta.marketplace.api.domain.*;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping(value = "/admin/softwares")
@RequiredArgsConstructor
public class AdminSoftwareController {

    private final SoftwareService softwareService;

    private final SoftwarePlanService softwarePlanService;

    //[1]Software
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
            @NotNull @Validated(Software.UpdateMetadata.class) @RequestBody Software software, BindingResult bindingResult)
            throws BindException {
        /*
        Software sameName = softwareService.getByName(software.getName());
        if (sameName != null && id != sameName.getId()) {
         bindingResult.rejectValue("name", "Unique");
         }
        */

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        software.setId(id);

        return softwareService.updateMetadata(software);
    }

    //[2]SoftwareHistory
    @GetMapping("/{id}/histories")
    public List<SoftwareHistory> getHistoryList(@NotNull @PathVariable Long id, Sort sort) {
        SoftwareHistorySpecification spec = new SoftwareHistorySpecification();
        spec.setSoftwareId(id);

        return softwareService.getHistoryList(spec, sort);
    }


    //[3]SoftwarePlan
    @GetMapping("/plan/{id}/list")
    public List<SoftwarePlan> currentSoftwarePlanList(@NotNull @PathVariable Long id) {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);
        return softwarePlanService.getCurrentSoftwarePlanList(spec);
    }

    @GetMapping("/plan/{id}/histories")
    public List<SoftwarePlan> getList(@NotNull @PathVariable Long id, Sort sort) {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);

        return softwarePlanService.getList(spec, sort);
    }

    @GetMapping("/plan/{id}/applyMonth")
    public List<SoftwarePlan> getApplyMonth(@NotNull @PathVariable Long id, @RequestParam(name="applyMonth") String applyMonth) {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);
        spec.setApplyMonth(applyMonth);
        return softwarePlanService.getApplyMonth(spec);
    }


}
