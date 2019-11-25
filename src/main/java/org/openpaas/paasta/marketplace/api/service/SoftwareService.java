package org.openpaas.paasta.marketplace.api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistory;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistorySpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.repository.SoftwareHistoryRepository;
import org.openpaas.paasta.marketplace.api.repository.SoftwarePlanRepository;
import org.openpaas.paasta.marketplace.api.repository.SoftwareRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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
        
        if (software.getName() != null) {
        	saved.setName(software.getName());
        }
        if (software.getCategory() != null) {
        	saved.setCategory(software.getCategory());
        }
        if (software.getApp() != null) {
        	saved.setApp(software.getApp());
        }
        if (software.getAppPath() != null) {
        	saved.setAppPath(software.getAppPath());
        }
        if (software.getManifest() != null) {
        	saved.setManifest(software.getManifest());
        }
        if (software.getManifestPath() != null) {
        	saved.setManifestPath(software.getManifestPath());
        }
        if (software.getScreenshotList() != null) {
        	saved.setScreenshotList(software.getScreenshotList());
        }
        if (software.getSummary() != null) {
        	saved.setSummary(software.getSummary());
        }
        if (software.getDescription() != null) {
        	saved.setDescription(software.getDescription());
        }
        if (software.getType() != null) {
        	saved.setType(software.getType());
        }
        if (software.getPricePerMonth() != null) {
        	saved.setPricePerMonth(software.getPricePerMonth());
        }
        if (software.getVersion() != null) {
        	saved.setVersion(software.getVersion());
        }
        if (software.getInUse() != null) {
        	saved.setInUse(software.getInUse());
        }
        if (software.getSoftwarePlanList() != null) {
        	saved.setSoftwarePlanList(software.getSoftwarePlanList());
        }
        saved.setIcon(software.getIcon());
        saved.setIconPath(software.getIconPath());        
        softwareRepository.save(saved);

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
    
    public Integer getSoldSoftwareCount(String userId, String status) {
    	return softwareRepository.getSoldSoftwareCount(userId, status);
    }
    
    /**
     * 카테고리를 사용하고 있는 소프트웨어 카운트
     * @param categoryId
     * @return
     */
    public Long getSoftwareUsedCategoryCount(Long categoryId) {
    	return softwareRepository.getSoftwareUsedCategoryCount(categoryId);
    }
    
    /**
     * 판매된 소프트웨어의 카운트정보 조회
     * @param softwareIdList
     * @return
     */
    public Map<String,Object> getSoftwareInstanceCountMap(List<Long> softwareIdList) {
    	List<Object[]> values = softwareRepository.getSoftwareInstanceCountMap(softwareIdList);
        return values.stream().collect(Collectors.toMap(v -> (String) v[0], v -> (BigDecimal) v[1]));
    }

}
