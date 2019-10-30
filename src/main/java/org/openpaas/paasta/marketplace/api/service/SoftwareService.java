package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.*;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
import org.openpaas.paasta.marketplace.api.repository.SoftwareHistoryRepository;
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
public class SoftwareService {

    //private final SwiftOSService swiftOSService;

    private final SoftwareRepository softwareRepository;

    private final SoftwarePlanRepository softwarePlanRepository;

    private final SoftwareHistoryRepository softwareHistoryRepository;

    public Software create(Software software) {
        software.setStatus(Status.Pending);

        return softwareRepository.save(software);
    }

    public Page<Software> getPage(SoftwareSpecification spec, Pageable pageable) {
        return softwareRepository.findAll(spec, pageable);
    }

    public Software get(Long id) {
        return softwareRepository.findById(id).get();
    }

    public Software getByName(String name) {
        return softwareRepository.findByName(name);
    }

    public Software update(Software software) {
        Software saved = softwareRepository.findById(software.getId()).get();

        List<SoftwarePlan> origin = software.getSoftwarePlanList();

        saved.setName(software.getName());
        saved.setCategory(software.getCategory());
        saved.setApp(software.getApp());
        saved.setAppPath(software.getAppPath());
        saved.setManifest(software.getManifest());
        saved.setManifestPath(software.getManifestPath());
        saved.setIcon(software.getIcon());
        saved.setIconPath(software.getIconPath());
        saved.setScreenshotList(software.getScreenshotList());
        saved.setSummary(software.getSummary());
        saved.setDescription(software.getDescription());
        saved.setType(software.getType());
        saved.setPricePerMonth(software.getPricePerMonth());
        saved.setVersion(software.getVersion());
        saved.setInUse(software.getInUse());
        saved.setSoftwarePlanList(software.getSoftwarePlanList());

        SoftwareHistory history = new SoftwareHistory();
        history.setSoftware(saved);
        history.setDescription(software.getHistoryDescription());
        softwareHistoryRepository.save(history);

        Long originPlanId;
        SoftwarePlan plan = new SoftwarePlan();

        for(int i = 0; i < origin.size(); i++) {
            originPlanId = origin.get(i).getId();
            plan = softwarePlanRepository.findById(originPlanId).get();
            plan.setName(software.getSoftwarePlanList().get(i).getName());
            plan.setDescription(software.getSoftwarePlanList().get(i).getDescription());
            plan.setCpuAmt(software.getSoftwarePlanList().get(i).getCpuAmt());
            plan.setMemorySize(software.getSoftwarePlanList().get(i).getMemorySize());
            plan.setMemoryAmt(software.getSoftwarePlanList().get(i).getMemoryAmt());
            plan.setDiskAmt(software.getSoftwarePlanList().get(i).getDiskAmt());
            plan.setDiskSize(software.getSoftwarePlanList().get(i).getDiskSize());
            plan.setApplyMonth(software.getSoftwarePlanList().get(i).getApplyMonth());

            softwarePlanRepository.save(plan);
        }

        return saved;
    }

    public Software updateMetadata(Software software) {
        Software saved = softwareRepository.findById(software.getId()).get();
        saved.setName(software.getName());
        saved.setCategory(software.getCategory());
        saved.setInUse(software.getInUse());
        if (software.getStatus() != saved.getStatus()) {
            saved.setStatusModifiedDate(LocalDateTime.now());
        }
        saved.setStatus(software.getStatus());
        saved.setConfirmComment(software.getConfirmComment());

        SoftwareHistory history = new SoftwareHistory();
        history.setSoftware(saved);
        history.setDescription(software.getHistoryDescription());
        softwareHistoryRepository.save(history);

        return saved;
    }

    public List<SoftwareHistory> getHistoryList(SoftwareHistorySpecification spec, Sort sort) {
        return softwareHistoryRepository.findAll(spec, sort);
    }

    public List<Software> getSwByCreatedBy(String providerId) {
        return softwareRepository.findByCreatedBy(providerId);
    }

}
