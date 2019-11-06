package org.openpaas.paasta.marketplace.api.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.InstanceCart;
import org.openpaas.paasta.marketplace.api.domain.InstanceCartSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.service.InstanceCartService;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/instances/cart")
@RequiredArgsConstructor
public class InstanceCartController {

	private final InstanceCartService instanceCartService;
	private final SoftwarePlanService softwarePlanService;
	
    @PostMapping
    public InstanceCart create(@NotNull @Validated @RequestBody InstanceCart instanceCart, BindingResult bindingResult) throws BindException {
        Software software = instanceCart.getSoftware();
        if (software.getId() == null) {
            bindingResult.rejectValue("software.id", "Required");
        }
        
        if (StringUtils.isBlank(instanceCart.getSoftwarePlanId())) {
        	bindingResult.rejectValue("softwarePlanId", "Required");
        }
        
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return instanceCartService.create(instanceCart);
    }
    
    @GetMapping("/allList")
    public List<InstanceCart> getAllList(InstanceCartSpecification spec) {
        spec.setCreatedBy(SecurityUtils.getUserId());
        List<InstanceCart> instanceCartList = instanceCartService.getAllList(spec);

        // softwarePlan의 가격정보 조회
        Long pricePerMonth = 0L;
        for (InstanceCart info : instanceCartList) {
        	pricePerMonth = softwarePlanService.getPricePerMonth(String.valueOf(info.getSoftware().getId()), info.getSoftwarePlanId());
        	info.setSoftwarePlanAmtMonth(pricePerMonth);
        }
        
        return instanceCartList;
    }
    
    @GetMapping("/userAllCartList")
    public List<InstanceCart> getAllList(@RequestParam(name="usageStartDate") String usageStartDate, @RequestParam(name="usageEndDate") String usageEndDate) {
    	// 해당 유저의 카트정보 조회 
    	List<InstanceCart> userAllCartList = instanceCartService.getUserAllCartList(SecurityUtils.getUserId(), usageStartDate, usageEndDate);
//    	System.out.println("#############################################################################");
//    	for (InstanceCart cartInfo : userAllCartList) {
//    		System.out.println(">>>>>>> cartInfo: "+ cartInfo.toString());
//    	}
//    	System.out.println("#############################################################################");
    	
    	return userAllCartList;
    }
    
    @DeleteMapping("/allDelete")
    public Integer allDelete(InstanceCartSpecification spec) {
    	spec.setCreatedBy(SecurityUtils.getUserId());
    	return instanceCartService.allDelete(spec);
    }
    
    @DeleteMapping("/delete")
    public Integer delete(InstanceCartSpecification spec) {
    	spec.setCreatedBy(SecurityUtils.getUserId());
    	return instanceCartService.delete(spec);
    }

}
