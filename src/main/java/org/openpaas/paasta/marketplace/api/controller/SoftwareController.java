package org.openpaas.paasta.marketplace.api.controller;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.*;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/softwares")
@RequiredArgsConstructor
public class SoftwareController {

    private final SoftwareService softwareService;

    private final SoftwarePlanService softwarePlanService;

    @GetMapping("/page")
    public Page<Software> getPage(SoftwareSpecification spec, Pageable pageable, HttpServletRequest httpServletRequest) {
        spec.setStatus(Status.Approval);
        spec.setInUse(Yn.Y);
        Page<Software> result = softwareService.getPage(spec, pageable);

        List<Software> softwareList = result.getContent();
        for (Software info : softwareList) {
        	info.setPricePerMonth(softwarePlanService.getMinPricePerMonth(String.valueOf(info.getId())));
        }

        return softwareService.getPage(spec, pageable);
    }

    @GetMapping("/my/page")
    public Page<Software> getMyPage(SoftwareSpecification spec, Pageable pageable) {
        spec.setCreatedBy(SecurityUtils.getUserId());

        return softwareService.getPage(spec, pageable);
    }

    @GetMapping("/{id}")
    public Software get(@NotNull @PathVariable Long id) {
        Software software = softwareService.get(id);
        software.setPricePerMonth(softwarePlanService.getMinPricePerMonth(String.valueOf(software.getId())));
        return software;
    }

    @PostMapping
    public Software create(@NotNull @Validated(Software.Create.class) @RequestBody Software software,
            BindingResult bindingResult) throws BindException {

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Software softwareAll = softwareService.create(software);

        //create plan
        for(int i = 0; i < software.getSoftwarePlanList().size(); i++) {
            software.getSoftwarePlanList().get(i).setSoftwareId(softwareAll.getId());
            softwarePlanService.create(software.getSoftwarePlanList().get(i));
        }
        return softwareAll;
    }

    @PutMapping("/{id}")
    public Software update(@PathVariable @NotNull Long id, @RequestParam(name ="softwarePlaneOriginalList", required=false) String softwarePlaneOriginalList,
            @NotNull @Validated(Software.Update.class) @RequestBody Software software, BindingResult bindingResult)
            throws BindException {
        List<SoftwarePlan> softwarePlans =software.getSoftwarePlanList();

        Software saved = softwareService.get(id);
        SecurityUtils.assertCreator(saved);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        software.setId(id);

        return softwareService.update(software,softwarePlaneOriginalList);
    }

    @GetMapping("/{id}/histories")
    public List<SoftwareHistory> getHistoryList(@NotNull @PathVariable Long id, Sort sort) {
        Software software = softwareService.get(id);
        SecurityUtils.assertCreator(software);

        SoftwareHistorySpecification spec = new SoftwareHistorySpecification();
        spec.setSoftwareId(id);

        return softwareService.getHistoryList(spec, sort);
    }
    
    /**
     * 판매자의 상태별 상품 갯수 조회
     * @param userId
     * @param status
     * @return
     */
    @GetMapping("/soldSoftwareCount")
    public Integer soldSoftwareCount(@RequestParam(name="userId") String userId, @RequestParam(name="status") String status) {
    	return softwareService.getSoldSoftwareCount(userId, status);
    }
    
    /**
     * 판매된 소프트웨어의 카운트정보 조회
     * @param softwareIdList
     * @return
     */
    @GetMapping("/instanceCount")
    public Map<String,Object> softwareInstanceCountMap(@RequestParam(name="softwareIdList") List<Long> softwareIdList) {
    	return softwareService.getSoftwareInstanceCountMap(softwareIdList);
    }

}
