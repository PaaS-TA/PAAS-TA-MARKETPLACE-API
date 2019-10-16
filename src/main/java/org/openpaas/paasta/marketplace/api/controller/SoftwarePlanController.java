package org.openpaas.paasta.marketplace.api.controller;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.*;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "/softwares/plan")
@RequiredArgsConstructor
public class SoftwarePlanController {

    private final SoftwareService softwareService;

    private final SoftwarePlanService softwarePlanService;

    @GetMapping("/{id}")
    public List<SoftwarePlan> getSoftwarePlanList(@NotNull @PathVariable Long id, Sort sort) {
        SoftwarePlan softwarePlan = softwarePlanService.get(id);
        SecurityUtils.assertCreator(softwarePlan);

        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);

        return softwarePlanService.getSoftwarePlanList(spec, sort);
    }

    @PostMapping
    public SoftwarePlan create(@NotNull @Validated @RequestBody SoftwarePlan softwarePlan,
                           BindingResult bindingResult) throws BindException {
        SoftwarePlan sameName = softwarePlanService.getByName(softwarePlan.getName());
        if (sameName != null) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return softwarePlanService.create(softwarePlan);
    }

    @PutMapping("/{id}")
    public SoftwarePlan update(@PathVariable @NotNull Long id,@RequestBody SoftwarePlan softwarePlan, BindingResult bindingResult) throws BindException {
        SoftwarePlan saved = softwarePlanService.get(id);
        SecurityUtils.assertCreator(saved);

        SoftwarePlan samePlanName = softwarePlanService.getByName(softwarePlan.getName());
        if (samePlanName != null && id != samePlanName.getId()) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        System.out.println(">> softwarePlanService.update");
        softwarePlanService.update(softwarePlan);
        return saved;
    }
}
