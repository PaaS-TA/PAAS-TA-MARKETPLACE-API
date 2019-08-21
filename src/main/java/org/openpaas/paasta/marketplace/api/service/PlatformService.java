package org.openpaas.paasta.marketplace.api.service;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.exception.PlatformException;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.NameType;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.AppService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PlatformService {

    @Value("${market.org..guid}")
    public String marketOrgGuid;

    @Value("${market.space.guid}")
    public String marketSpaceGuid;

    @Value("${market.naming-type}")
    public NameType localNamingType;

    private final AppService appService;

    private final SoftwareService softwareService;

    public void provision(Instance instance) {



        Software software = instance.getSoftware();
        String name = generateName(instance);

        //software.setName(name);

        // 1) 앱 생성하는 CF 호출
        Map<String, Object> result = appService.createApp(software, name);

        // 2) 나머지 값 채워주기
        String appGuid = result.get("appId").toString();

        instance.setAppGuid(appGuid);
        instance.setAppName(name);


//        Map createdApplication = appService.createApp(parseSoftware(software, name));
//        appService.initEnv(createdApplication.getGuid(), instance.getEnv());
//
//        String appGuid = createdApplication.getGuid();
//        appService.createService(appGuid, instance.getId(), instance.getPlanGuid());
//
//        getAppStats(appGuid, name);

    }

    public void deprovision(Instance instance) throws PlatformException {
        // TODO: implements
    }


    String generateName(Instance instance) {
        return localNamingType.generateName(instance);
    }

}
