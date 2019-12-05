package org.openpaas.paasta.marketplace.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.validation.constraints.NotNull;

import org.openpaas.paasta.marketplace.api.cloudFoundryModel.NameType;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistory;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistorySpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlanSpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.TestSoftwareInfo;
import org.openpaas.paasta.marketplace.api.exception.PlatformException;
import org.openpaas.paasta.marketplace.api.service.PlatformService;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.service.TestSoftwareInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/admin/softwares")
@RequiredArgsConstructor
public class AdminSoftwareController {

    @Value("${market.naming-type}")
    public NameType localNamingType;

    private final SoftwareService softwareService;

    private final SoftwarePlanService softwarePlanService;

    private final PlatformService platformService;

    private final TestSoftwareInfoService testSoftwareInfoService;

    //[1]Software
    @GetMapping("/page")
    public Page<Software> getPage(SoftwareSpecification spec, Pageable pageable) {
        return softwareService.getPage(spec, pageable);
    }

    @GetMapping("/{id}")
    public Software get(@NotNull @PathVariable Long id) {
        return softwareService.get(id);
    }

    @PutMapping("/{id}")
    public Software update(@PathVariable @NotNull Long id,
            @NotNull @Validated(Software.UpdateMetadata.class) @RequestBody Software software, BindingResult bindingResult)
            throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        software.setId(id);

        return softwareService.updateMetadata(software);
    }

    //[2]SoftwareHistory
    @GetMapping("/{id}/histories")
    public List<SoftwareHistory> getHistoryList(@NotNull @PathVariable Long id, Sort sort) {
        SoftwareHistorySpecification spec = new SoftwareHistorySpecification();
        spec.setSoftwareId(id);

        return softwareService.getHistoryList(spec, sort);
    }


    //[3]SoftwarePlan
    @GetMapping("/plan/{id}/list")
    public List<SoftwarePlan> currentSoftwarePlanList(@NotNull @PathVariable Long id) {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);
        return softwarePlanService.getCurrentSoftwarePlanList(spec);
    }

    /**
     * 상품가격 리스트 조회
     * @param id
     * @param sort
     * @return
     */
    @GetMapping("/plan/{id}/histories")
    public List<SoftwarePlan> getList(@NotNull @PathVariable Long id, Sort sort) {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);

        return softwarePlanService.getList(spec, sort);
    }

    /**
     * 상품가격 정보를 적용일자로 조회
     * @param id
     * @param applyMonth
     * @return
     */
    @GetMapping("/plan/{id}/applyMonth")
    public List<SoftwarePlan> getApplyMonth(@NotNull @PathVariable Long id, @RequestParam(name="applyMonth") String applyMonth) {
        SoftwarePlanSpecification spec = new SoftwarePlanSpecification();
        spec.setSoftwareId(id);
        spec.setApplyMonth(applyMonth);
        return softwarePlanService.getApplyMonth(spec);
    }

    /**
     * 앱 배포 테스트
     *
     * @param id
     * @param planId
     * @param testSoftwareInfo
     * @return
     * @throws PlatformException
     */
    @PostMapping("/{id}/plan/{planId}")
    public TestSoftwareInfo deployTestSoftware(@PathVariable Long id, @PathVariable Long planId, @RequestBody TestSoftwareInfo testSoftwareInfo) throws PlatformException {
        Random rnd = new Random();
        String randomNum = "" + rnd.nextInt(10000);

        if(randomNum.length() != 4) {
            int addNum = 4 - randomNum.length();
            if(addNum > 0) {
                for(int i = 0; i < addNum; i++) {
                    randomNum = "1" + randomNum;
                }
            }
        }

        Software software = softwareService.get(id);

        Instance instance = new Instance();
        instance.setId(Long.valueOf(randomNum));
        instance.setAppName(testSoftwareInfo.getName());
        instance.setSoftware(software);
        instance.setSoftwarePlanId(String.valueOf(planId));


        String name = localNamingType.generateName(instance, testSoftwareInfo.getName());

        testSoftwareInfo.setName(name);
        testSoftwareInfo.setSoftwareId(software.getId());
        testSoftwareInfo.setSoftwarePlanId(planId);

        String appGuid = null;

        try{
            appGuid = platformService.provision(instance, true);
            testSoftwareInfo.setAppGuid(appGuid);
            testSoftwareInfo.setStatus(TestSoftwareInfo.Status.Successful);
        }catch (PlatformException e) {
            testSoftwareInfo.setAppGuid(e.getMessage());
            testSoftwareInfo.setStatus(TestSoftwareInfo.Status.Failed);
            //throw new PlatformException("appGuid doesn't exist!!!");
        }

        return testSoftwareInfoService.create(testSoftwareInfo);
    }


    /**
     * 각 상품에 대한 배포 테스한 앱 목록 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/testSwInfo")
    public List<TestSoftwareInfo> getTestSwInfoList(@PathVariable Long id) {
        return testSoftwareInfoService.getTestSwInfoList(id);
    }

    /**
     * 배포 테스트한 앱 삭제
     *
     * @param swId
     * @param id
     * @param appGuid
     * @return
     */
    @DeleteMapping("/{swId}/testSwInfo/{id}/app/{appGuid}")
    public Map deleteDeployTestApp(@PathVariable Long swId, @PathVariable Long id, @PathVariable String appGuid) {
        Software software = softwareService.get(swId);
        Instance instance = new Instance();

        instance.setSoftware(software);
        instance.setAppGuid(appGuid);

        Map resultMap = new HashMap();

        try {
            platformService.deprovision(instance);
            testSoftwareInfoService.deleteDeployTestApp(id);
            resultMap.put("RESULT", TestSoftwareInfo.Status.Successful);

        } catch (PlatformException e) {
            //e.printStackTrace();
            resultMap.put("RESULT", TestSoftwareInfo.Status.Failed);
        }

        return resultMap;
    }

    /**
     * 배포 테스트 실패한 앱 삭제
     * @param testFailedAppId
     * @return
     */
    @DeleteMapping("/testFailed/app/{testFailedAppId}")
    public Long deleteDeployTestFailedApp(@PathVariable Long testFailedAppId) {
    	long deleteCount = 0;
    	
    	try {
    		testSoftwareInfoService.deleteDeployTestApp(testFailedAppId);
    		deleteCount = 1;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return deleteCount;
    }

    /**
     * 카테고리를 사용하고 있는 소프트웨어 카운트
     * @param categoryid
     * @return
     */
    @GetMapping("/softwareUsedCategoryCount/{categoryid}")
    public Long softwareUsedCategoryCount(@PathVariable Long categoryid) {
    	return softwareService.getSoftwareUsedCategoryCount(categoryid);
    }
}
