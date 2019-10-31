package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public Software update(Software software, String softwarePlaneOriginalList) {

        Software saved = softwareRepository.findById(software.getId()).get();

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

        //SW-plan Data
        String [] arrSoftwarePlaneOriginal = StringUtils.split(softwarePlaneOriginalList,"\\^");
        Map<String, String> updatePlanMap = new HashMap<String,String>();
        List<SoftwarePlan> procPlanList = software.getSoftwarePlanList();

        if(procPlanList != null){
            for (SoftwarePlan targetPlan : procPlanList) {
                // 1. update - id map에 추가
                if (targetPlan.getId() != null && targetPlan.getId() != 0) {
                    softwarePlanRepository.save(targetPlan);
                    updatePlanMap.put(String.valueOf(targetPlan.getId()), "Y");
                } else {
                    // 2. insert
                    softwarePlanRepository.save(targetPlan);
                }
            }
        }
        // delete
        if(arrSoftwarePlaneOriginal != null && arrSoftwarePlaneOriginal.length > 0){
            for (String deleteTargetId : arrSoftwarePlaneOriginal) {
                if (StringUtils.isBlank(updatePlanMap.get(deleteTargetId))) {
                    // Delete 실행
                    SoftwarePlan softwarePlan = new SoftwarePlan();
                    softwarePlan.setId(Long.valueOf(deleteTargetId));
                    softwarePlanRepository.delete(softwarePlan);
                }
            }
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
