package org.openpaas.paasta.marketplace.api.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.InstanceCartSpecification;
import org.openpaas.paasta.marketplace.api.domain.InstanceSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.service.InstanceService;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/instances")
@RequiredArgsConstructor
public class InstanceController {

    private final InstanceService instanceService;
    private final SoftwarePlanService softwarePlanService;

    @GetMapping("/page")
    public Page<Instance> getPage(InstanceSpecification spec, Pageable pageable) {
        return instanceService.getPage(spec, pageable);
    }

    @GetMapping("/my/page")
    public Page<Instance> getMyPage(InstanceSpecification spec, Pageable pageable) {
        spec.setCreatedBy(SecurityUtils.getUserId());
        spec.setStatus(Instance.Status.Approval);
//        return instanceService.getPage(spec, pageable);
        
        Page<Instance> result = instanceService.getPage(spec, pageable);
        
        // softwarePlan의 가격정보 조회
        List<Instance> instanceList = result.getContent();
        Long pricePerMonth = 0L;
        for (Instance instance : instanceList) {
        	pricePerMonth = softwarePlanService.getPricePerMonth(String.valueOf(instance.getSoftware().getId()), instance.getSoftwarePlanId());
        	instance.getSoftware().setPricePerMonth(pricePerMonth);
        }
        
        return result;
    }

    @GetMapping("/my/totalPage")
    public Page<Instance> getMyTotalPage(InstanceSpecification spec, Pageable pageable) {
        spec.setCreatedBy(SecurityUtils.getUserId());
        Page<Instance> result = instanceService.getPage(spec, pageable);
        
        // softwarePlan의 가격정보 조회
        List<Instance> instanceList = result.getContent();
        Long pricePerMonth = 0L;
        for (Instance instance : instanceList) {
        	pricePerMonth = softwarePlanService.getPricePerMonth(String.valueOf(instance.getSoftware().getId()), instance.getSoftwarePlanId());
        	instance.getSoftware().setPricePerMonth(pricePerMonth);
        }
        
        return result;
    }

//    @GetMapping("/my/page/month")
//    public Page<Instance> getMyMonth(@RequestParam LocalDateTime usageStartDate, @RequestParam LocalDateTime usageEndDate, Pageable pageable) {
//        //spec.setCreatedBy(SecurityUtils.getUserId());
//        return instanceService.getPage2(SecurityUtils.getUserId(), usageStartDate, usageEndDate, pageable);
//    }

    @GetMapping("/{id}")
    public Instance get(@NotNull @PathVariable Long id) {
//        return instanceService.get(id);
        
        Instance instance = instanceService.get(id);
        
        // softwarePlan의 가격정보 조회
        Long pricePerMonth = softwarePlanService.getPricePerMonth(String.valueOf(instance.getSoftware().getId()), instance.getSoftwarePlanId());
    	instance.getSoftware().setPricePerMonth(pricePerMonth);
    	return instance;
    }

    @PostMapping
    public Instance create(@NotNull @Validated @RequestBody Instance instance, BindingResult bindingResult)
            throws BindException {
        Software software = instance.getSoftware();
        if (software.getId() == null) {
            bindingResult.rejectValue("software.id", "Required");
        }
        
        if (StringUtils.isBlank(instance.getSoftwarePlanId())) {
        	bindingResult.rejectValue("softwarePlanId", "Required");
        }
        
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return instanceService.create(instance);
    }

    @DeleteMapping("/{id}")
    public Instance delete(@PathVariable @NotNull Long id) throws BindException {
        Instance saved = instanceService.get(id);
        SecurityUtils.assertCreator(saved);

        instanceService.updateToDeleted(id);

        return saved;
    }
    
    @GetMapping("/usagePriceTotal")
    public Long usagePriceTotal(InstanceCartSpecification spec) throws BindException {
    	spec.setCreatedBy(SecurityUtils.getUserId());
    	return instanceService.usagePriceTotal(spec);
    }

}
