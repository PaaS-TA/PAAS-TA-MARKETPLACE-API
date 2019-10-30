package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.*;
import org.openpaas.paasta.marketplace.api.repository.SoftwarePlanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class SoftwarePlanService {

    //private final SwiftOSService swiftOSService;

    private final SoftwarePlanRepository softwarePlanRepository;

    public SoftwarePlan get(Long id) {
        return softwarePlanRepository.findBySoftwareId(id);
    }

    public SoftwarePlan getSWPId(Long id) {
        return softwarePlanRepository.findById(id).get();
    }

    public SoftwarePlan create(SoftwarePlan softwarePlan) {
        return softwarePlanRepository.save(softwarePlan);
    }

    public SoftwarePlan getByName(String name) {
        return softwarePlanRepository.findByName(name);
    }

    public Page<SoftwarePlan> getPage(SoftwarePlanSpecification spec, Pageable pageable) {
        return softwarePlanRepository.findAll(spec, pageable);
    }

    public SoftwarePlan update(SoftwarePlan softwarePlan) {
        System.out.println(">> update Init");
        SoftwarePlan saved = softwarePlanRepository.findBySoftwareId(softwarePlan.getSoftwareId());
        saved.setName(softwarePlan.getName());
        saved.setId(softwarePlan.getId());
        saved.setApplyMonth(softwarePlan.getApplyMonth());
        saved.setDescription(softwarePlan.getDescription());
        saved.setMemorySize(softwarePlan.getMemorySize());
        saved.setDiskSize(softwarePlan.getDiskSize());
        saved.setCpuAmt(softwarePlan.getCpuAmt());
        saved.setDiskAmt(softwarePlan.getDiskAmt());
        saved.setInUse(softwarePlan.getInUse());

        System.out.println(">> save" + saved.toString());
        return saved;
    }

    public List<SoftwarePlan> getList(SoftwarePlanSpecification spec, Sort sort) {
    	return softwarePlanRepository.findAll(spec, sort);
    }

    public void delete(Long id) {
        softwarePlanRepository.deleteById(id);
    }

    public Long getPricePerMonth(String softwareId, String softwarePlaneId) {
    	return softwarePlanRepository.pricePerMonth(softwareId, softwarePlaneId);
    }

    public List<SoftwarePlan> getCurrentSoftwarePlanList(SoftwarePlanSpecification spec) {
    	return softwarePlanRepository.findCurrentSoftwarePlanList(spec.getSoftwareId());
    }

    public List<SoftwarePlan> getApplyMonth(SoftwarePlanSpecification spec) {
        return softwarePlanRepository.findAll(spec);
    }

    public Long getMinPricePerMonth(String softwareId) {
        return softwarePlanRepository.minPricePerMonth(softwareId);
    }
}
