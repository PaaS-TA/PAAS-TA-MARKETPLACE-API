package org.openpaas.paasta.marketplace.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.InstanceCart;
import org.openpaas.paasta.marketplace.api.domain.InstanceCartSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.service.InstanceCartService;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
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
	
	/**
	 * 장바구니에 상품 담기
	 * @param instanceCart
	 * @param bindingResult
	 * @return
	 * @throws BindException
	 */
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
    
    /**
     * User의 모든 장바구니 리스트를 조건없이 조회
     * @param spec
     * @return
     */
    @GetMapping("/allList")
    public List<InstanceCart> getAllList(InstanceCartSpecification spec) {
        spec.setCreatedBy(SecurityUtils.getUserId());
        List<InstanceCart> instanceCartList = instanceCartService.getAllList(spec);

        if (CollectionUtils.isEmpty(instanceCartList)) {
    		return instanceCartList;
    	}

        // softwarePlan의 ID정보 리스트 생성
    	List<Long> inSoftwarePlanId = new ArrayList<Long>();
    	for (InstanceCart info : instanceCartList) {
    		inSoftwarePlanId.add(Long.valueOf(info.getSoftwarePlanId()));
    	}

    	// softwarePlan의 가격정보 조회
    	Map<String,Long> planInfoMap = softwarePlanService.getPricePerMonthList(inSoftwarePlanId);
    	if (CollectionUtils.isEmpty(planInfoMap)) {
    		return instanceCartList;
    	}
    	
    	// 장바구니 상품정보에  Plan정보 설정
    	Long pricePerMonth = 0L;
		for (InstanceCart info : instanceCartList) {
			pricePerMonth = planInfoMap.get(info.getSoftwarePlanId());
			if (pricePerMonth != null) {
				info.setSoftwarePlanAmtMonth(pricePerMonth);
			}
    	}
        
        return instanceCartList;
    }
    
    /**
     * User의 장바구니 리스트를 검색일 조건으로 조회
     * @param usageStartDate
     * @param usageEndDate
     * @return
     */
    @GetMapping("/userAllCartList")
    public List<InstanceCart> getAllList(@RequestParam(name="usageStartDate") String usageStartDate, @RequestParam(name="usageEndDate") String usageEndDate) {
    	// 해당 유저의 카트정보 조회 
    	List<InstanceCart> userAllCartList = instanceCartService.getUserAllCartList(SecurityUtils.getUserId(), usageStartDate, usageEndDate);
    	return userAllCartList;
    }
    
    /**
     * User의 모든 장바구니 데이터를 삭제
     * @param spec
     * @return
     */
    @DeleteMapping("/allDelete")
    public Integer allDelete(InstanceCartSpecification spec) {
    	spec.setCreatedBy(SecurityUtils.getUserId());
    	return instanceCartService.allDelete(spec);
    }
    
    /**
     * User의 장바구니상품을 선택적으로 삭제
     * @param spec
     * @return
     */
    @DeleteMapping("/delete")
    public Integer delete(InstanceCartSpecification spec) {
    	spec.setCreatedBy(SecurityUtils.getUserId());
    	return instanceCartService.delete(spec);
    }
    
    /**
     * User의 장바구니 리스트 조회
     * @param spec
     * @return
     */
    @GetMapping("/page/list")
    public List<InstanceCart> cartPageList(InstanceCartSpecification spec, Sort sort) {
    	// 장바구니 상품 리스트 조회
        spec.setCreatedBy(SecurityUtils.getUserId());
        List<InstanceCart> instanceCartList = instanceCartService.getAllList(spec, sort);

        if (CollectionUtils.isEmpty(instanceCartList)) {
        	return instanceCartList;
        }
        
        // softwarePlan의 ID정보 리스트 생성
    	List<Long> inSoftwarePlanId = new ArrayList<Long>();
    	for (InstanceCart info : instanceCartList) {
    		inSoftwarePlanId.add(Long.valueOf(info.getSoftwarePlanId()));
    	}

    	// softwarePlan의 가격정보 조회
    	Map<String,Long> planInfoMap = softwarePlanService.getPricePerMonthList(inSoftwarePlanId);
    	if (CollectionUtils.isEmpty(planInfoMap)) {
    		return instanceCartList;
    	}
    	
    	// 장바구니 상품정보에  Plan정보 설정
    	Long pricePerMonth = 0L;
		for (InstanceCart info : instanceCartList) {
			pricePerMonth = planInfoMap.get(info.getSoftwarePlanId());
			if (pricePerMonth != null) {
				info.setSoftwarePlanAmtMonth(pricePerMonth);
			}
    	}
        
        return instanceCartList;
    }

}
