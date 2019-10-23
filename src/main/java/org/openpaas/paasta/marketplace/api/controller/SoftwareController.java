package org.openpaas.paasta.marketplace.api.controller;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.*;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "/softwares")
@RequiredArgsConstructor
public class SoftwareController {

    private final SoftwareService softwareService;

    private final SoftwarePlanService softwarePlanService;

    @GetMapping("/page")
    public Page<Software> getPage(SoftwareSpecification spec, Pageable pageable, HttpServletRequest httpServletRequest) {
        //System.out.println("bearer 토큰 ::: " + httpServletRequest.getHeader("cf-Authorization"));

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

        Software softwareAll = softwareService.create(software);

        //create plan
        for(int i = 0; i < software.getSoftwarePlanList().size(); i++) {
            software.getSoftwarePlanList().get(i).setSoftwareId(softwareAll.getId());
            softwarePlanService.create(software.getSoftwarePlanList().get(i));
        }
        return softwareAll;
    }

    @PutMapping("/{id}")
    public Software update(@PathVariable @NotNull Long id,
            @NotNull @Validated(Software.Update.class) @RequestBody Software software, BindingResult bindingResult)
            throws BindException {
        System.out.println("[Init]: " + software.toString());
        List<SoftwarePlan> softwarePlans =software.getSoftwarePlanList();

        for (SoftwarePlan softwarePlan:softwarePlans) {
            System.out.println("[softwarePlan.toString() For] " + softwarePlan.toString());
        }

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
