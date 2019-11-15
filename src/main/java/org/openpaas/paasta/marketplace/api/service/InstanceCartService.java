package org.openpaas.paasta.marketplace.api.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.InstanceCart;
import org.openpaas.paasta.marketplace.api.domain.InstanceCartSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.Yn;
import org.openpaas.paasta.marketplace.api.repository.InstanceCartRepository;
import org.openpaas.paasta.marketplace.api.util.HostUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InstanceCartService {

    @Value("${provisioning.try-count}")
    private int provisioningTryCount;

    @Value("${provisioning.timeout}")
    private long provisioningTimeout;

    @Value("${deprovisioning.try-count}")
    private int deprovisioningTryCount;

    @Value("${deprovisioning.timeout}")
    private long deprovisioningTimeout;

    private final InstanceCartRepository instanceCartRepository;
    

    public InstanceCart create(InstanceCart instanceCart) {
        instanceCart.setHost(HostUtils.getHostName());
        instanceCart.setInUse(Yn.N);
        return instanceCartRepository.save(instanceCart);
    }
    
    public List<InstanceCart> getAllList(InstanceCartSpecification spec) {
        return instanceCartRepository.findAll(spec);
    }
    
    public List<InstanceCart> getAllList(InstanceCartSpecification spec, Sort sort) {
    	return instanceCartRepository.findAll(spec, sort);
    }
    
    public List<InstanceCart> getUserAllCartList(String userId, String usageStartDate, String usageEndDate) {
    	List<InstanceCart> result = new ArrayList<InstanceCart>();
    	List<Object[]> tempList = instanceCartRepository.userAllCartList(userId, usageStartDate, usageEndDate);
    	
    	if (tempList != null && !tempList.isEmpty()) {
    		for (Object[] arrInfo : tempList) {
    			InstanceCart instanceCart = new InstanceCart();
    			Software software = new Software();
    			Category category = new Category();
    			
    			instanceCart.setId(Long.valueOf((String) arrInfo[0])); 	// instanceCartId
    			software.setId(Long.valueOf((String) arrInfo[1])); 		// softwareId
    			software.setName((String) arrInfo[2]); 					// softwareName
    			software.setVersion((String) arrInfo[3]); 				// softwareVersion
    			category.setId(Long.valueOf((String) arrInfo[4])); 		// categoryId
    			category.setName((String) arrInfo[5]); 					// categoryName
    			instanceCart.setSoftwarePlanAmtMonth(Long.valueOf((String) arrInfo[6]));// softwarePlanAmtMonth
    			instanceCart.setPricePerInstance(Long.valueOf((String) arrInfo[7])); 	// pricePerInstance
    			
    			software.setCategory(category);
    			instanceCart.setSoftware(software);
    			result.add(instanceCart);
    		}
    	}
    	
    	return result;
    }
    
    public Integer allDelete(InstanceCartSpecification spec) {
    	if (StringUtils.isBlank(spec.getCreatedBy())) {
    		return 0;
    	}
    	return instanceCartRepository.deleteAllByUserIdInQuery(spec.getCreatedBy());
    }
    
    public Integer delete(InstanceCartSpecification spec) {
    	if (StringUtils.isBlank(spec.getCreatedBy())) {
    		return 0;
    	}
    	if (spec.getInInstanceCartId() == null || spec.getInInstanceCartId().isEmpty()) {
    		return 0;
    	}
    	return instanceCartRepository.delete(spec.getCreatedBy(), spec.getInInstanceCartId());
    }
}
