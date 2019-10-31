package org.openpaas.paasta.marketplace.api.controller;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.*;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
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

    private final SoftwarePlanService softwarePlanService;

    @GetMapping("/{id}")
    public SoftwarePlan get(@NotNull @PathVariable Long id) {
        return softwarePlanService.get(id);
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
    public SoftwarePlan update(@PathVariable @NotNull Long id, @NotNull @Validated(SoftwarePlan.Update.class)
                               @RequestBody SoftwarePlan softwarePlan) {
        SoftwarePlan saved = softwarePlanService.get(id);
        SecurityUtils.assertCreator(saved);

        softwarePlan.setSoftwareId(id);
        System.out.println(">> softwarePlanService.update");
        return softwarePlanService.update(softwarePlan);
    }

    @GetMapping("/{id}/list")
    public List<SoftwarePlan> currentSoftwarePlanList(@NotNull @PathVariable Long id) {
    	SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
    	spec.setSoftwareId(id);
    	return softwarePlanService.getCurrentSoftwarePlanList(spec);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        softwarePlanService.delete(id);
    }

    @GetMapping("/pricePerMonth")
    public void pricePerMonth(@RequestParam(name="softwareId") String softwareId, @RequestParam(name="softwarePlanId") String softwarePlanId) {
    	softwarePlanService.getPricePerMonth(softwareId, softwarePlanId);
    }

    @GetMapping("/{id}/histories")
    public List<SoftwarePlan> getList(@NotNull @PathVariable Long id, Sort sort) {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);

        return softwarePlanService.getList(spec, sort);
    }

    @GetMapping("/{id}/applyMonth")
    public List<SoftwarePlan> getApplyMonth(@NotNull @PathVariable Long id, @RequestParam(name="applyMonth") String applyMonth) {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);
        spec.setApplyMonth(applyMonth);
        return softwarePlanService.getApplyMonth(spec);
    }
    
    @GetMapping("/minPricePerMonth")
    public Long minPricePerMonth(@RequestParam(name="softwareId") String softwareId) {
        return softwarePlanService.getMinPricePerMonth(softwareId);
    }
}
