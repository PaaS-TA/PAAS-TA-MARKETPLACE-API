package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.*;
import org.openpaas.paasta.marketplace.api.repository.SoftwarePlanRepository;
import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SoftwarePlanService {

    //private final SwiftOSService swiftOSService;
    private final SoftwareRepository softwareRepository;

    private final SoftwarePlanRepository softwarePlanRepository;

    public SoftwarePlan get(Long id) {
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

    public List<SoftwarePlan> getSoftwarePlanList(SoftwarePlanSpecification spec, Sort sort) {
        return softwarePlanRepository.findAll(spec, sort);
    }

    public SoftwarePlan update(SoftwarePlan softwarePlan) {

        System.out.println(">> update Init");
        SoftwarePlan saved = softwarePlanRepository.findById(softwarePlan.getId()).get();
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

}
