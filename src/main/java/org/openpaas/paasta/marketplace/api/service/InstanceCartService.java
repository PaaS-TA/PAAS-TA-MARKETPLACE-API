package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.InstanceCart;
import org.openpaas.paasta.marketplace.api.domain.InstanceCartSpecification;
import org.openpaas.paasta.marketplace.api.domain.Yn;
import org.openpaas.paasta.marketplace.api.repository.InstanceCartRepository;
import org.openpaas.paasta.marketplace.api.util.HostUtils;
import org.springframework.beans.factory.annotation.Value;
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
    
    public Integer allDelete(InstanceCartSpecification spec) {
    	if (StringUtils.isBlank(spec.getCreatedBy())) {
    		return 0;
    	}
    	return instanceCartRepository.deleteAllByUserIdInQuery(spec.getCreatedBy());
    }
}
