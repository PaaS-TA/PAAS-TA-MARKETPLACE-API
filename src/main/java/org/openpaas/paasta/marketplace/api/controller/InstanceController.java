package org.openpaas.paasta.marketplace.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.InstanceSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.service.InstanceService;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        Page<Instance> result = instanceService.getPage(spec, pageable);
        
        // softwarePlan의 가격정보 조회
//        List<Instance> instanceList = result.getContent();
//        Long pricePerMonth = 0L;
//        if (instanceList != null && !instanceList.isEmpty()) {
//	        for (Instance instance : instanceList) {
//	        	pricePerMonth = softwarePlanService.getPricePerMonth(String.valueOf(instance.getSoftware().getId()), instance.getSoftwarePlanId());
//	        	instance.getSoftware().setPricePerMonth(pricePerMonth);
//	        }
//        }
        
        return result;
    }

    @GetMapping("/my/totalPage")
    public Page<Instance> getMyTotalPage(InstanceSpecification spec, Pageable pageable) {
        spec.setCreatedBy(SecurityUtils.getUserId());
        Page<Instance> result = instanceService.getPage(spec, pageable);
        
        // softwarePlan의 가격정보 조회
//        List<Instance> instanceList = result.getContent();
//        Long pricePerMonth = 0L;
//        for (Instance instance : instanceList) {
//        	pricePerMonth = softwarePlanService.getPricePerMonth(String.valueOf(instance.getSoftware().getId()), instance.getSoftwarePlanId());
////        	instance.getSoftware().setPricePerMonth(softwarePlanService.getPricePerMonth(String.valueOf(instance.getSoftware().getId()), instance.getSoftwarePlanId()));
//        	instance.getSoftware().setSoftwarePlanAmtMonth(pricePerMonth);
//        	instance.setSoftwarePlanAmtMonth(pricePerMonth);
//        }
        
        // 상품리스트
        List<Instance> instanceList = result.getContent();
        if (CollectionUtils.isEmpty(instanceList)) {
    		return result;
    	}
        
        // softwarePlan의 ID정보 리스트 생성
    	List<Long> inSoftwarePlanId = new ArrayList<Long>();
    	for (Instance info : instanceList) {
    		inSoftwarePlanId.add(Long.valueOf(info.getSoftwarePlanId()));
    	}

    	// softwarePlan의 가격정보 조회
    	Map<String,Long> planInfoMap = softwarePlanService.getPricePerMonthList(inSoftwarePlanId);
    	if (CollectionUtils.isEmpty(planInfoMap)) {
    		return result;
    	}
    	
    	// 장바구니 상품정보에  Plan정보 설정
    	Long pricePerMonth = 0L;
		for (Instance info : instanceList) {
			pricePerMonth = planInfoMap.get(info.getSoftwarePlanId());
			if (pricePerMonth != null) {
				info.getSoftware().setSoftwarePlanAmtMonth(pricePerMonth);
				info.setSoftwarePlanAmtMonth(pricePerMonth);
			}
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
    public Long usagePriceTotal(@RequestParam(name = "usageStartDate", required = false) String usageStartDate
    							,@RequestParam(name = "usageEndDate", required = false) String usageEndDate) throws BindException {
    	return instanceService.usagePriceTotal(SecurityUtils.getUserId(), usageStartDate, usageEndDate);
    }

    @GetMapping("/pricePerInstanceList")
    public Map<String, String> pricePerInstanceList(
    		@RequestParam(name = "inInstanceId", required = false) List<Long> inInstanceId
    		,@RequestParam(name = "usageStartDate", required = false) String usageStartDate
    		,@RequestParam(name = "usageEndDate", required = false) String usageEndDate) {
    	
    	if (inInstanceId == null || inInstanceId.isEmpty()) {
    		return new HashMap<String, String>();
    	}
    	if (StringUtils.isBlank(usageStartDate) || StringUtils.isBlank(usageEndDate)) {
    		return new HashMap<String, String>();
    	}

        return instanceService.getPricePerInstanceList(inInstanceId, usageStartDate, usageEndDate);
    }

}
