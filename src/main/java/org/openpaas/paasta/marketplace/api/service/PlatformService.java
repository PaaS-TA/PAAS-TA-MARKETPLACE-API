package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.NameType;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.exception.PlatformException;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.AppService;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.ServiceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Value("${cloudfoundry.cc.api.host}")
    public String cfHostName;

    private final AppService appService;

    private final ServiceService serviceService;

    public void provision(Instance instance) throws PlatformException {

        Software software = instance.getSoftware();
        String name = generateName(instance);

        // 1) 앱 생성하는 CF 호출(처음에는 app no start)
        Map<String, Object> result = appService.createApp(software, name);
        String appGuid = result.get("appId").toString();


        // 환경변수 업데이트
        Map env = (Map) result.get("env");
        appService.updateApp(env, appGuid);


        // 2) 서비스 요청이 있을 경우
        if(env.containsKey("services")){
            procServiceBinding(instance, env, appGuid);
        }


        // 3) 나머지 값 채워주기
        instance.setAppGuid(appGuid);
        instance.setAppName(name);
        instance.setAppUrl(name + cfHostName);


        // 4) 앱 시작 및 상태 체크
        getAppStats(appGuid, name);

    }

    public void deprovision(Instance instance) throws PlatformException {
        // TODO: implements
    }


    String generateName(Instance instance) {
        return localNamingType.generateName(instance);
    }




    private void procServiceBinding(Instance instance, Map env, String appGuid){
        List<String> services = (List) env.get("services");
        System.out.println("services ::: " + services.toString());

        // 플랜 아이디로 서비스 생성
        int index = 0;

        // services 에 서비스 타입 판별 후 해당하는 서비스 브로커 존재 여부 -> 있으면 서비스 플랜 아이디 조회
        ListServiceBrokersResponse brokers = serviceService.getServiceBrokers();
        ServiceBrokerResource broker = null;
        Map<Integer, List> planMap = new HashMap();

        for (String serviceName : services) {
            index++;
            for(ServiceBrokerResource resource : brokers.getResources()){
                List<String> planIdList = new ArrayList<>();
                broker = resource;

                if(broker.getEntity().getName().toLowerCase().contains(serviceName.toLowerCase())){
                    System.out.println("브로커 아이디이이이 ::: " + broker.getMetadata().getId());
                    System.out.println("서비스 플랜들..." + serviceService.getServicePlans(broker.getMetadata().getId()).getResources().toString());

                    ListServicePlansResponse planList = serviceService.getServicePlans(broker.getMetadata().getId());
                    for (ServicePlanResource plan : planList.getResources()) {
                        planIdList.add(plan.getMetadata().getId());
                    }

                    planMap.put(index, planIdList);
                }
            }

        }

        // 서비스 인스턴스 생성
        List createdService = new ArrayList();

        if(planMap.size() > 0){
            for(int i = 0; i < planMap.size(); i++) {
                String planId = planMap.get(i+1).get(0).toString();
                System.out.println("service instance id ::: " + planId);
                String serviceName = String.format("service-%d-%d", instance.getId(), i);

                createdService.add(i, serviceService.createServiceInstance(serviceName, appGuid, planId));
            }
        }

        System.out.println("createdService ::: " + createdService.get(0).toString());


        // 앱 아이디와 서비스 아이디로 바인딩
        if(createdService.size() > 0){
            System.out.println("여기로 들어왔녜???");
            for (int j = 0; j < createdService.size(); j++){
                String serviceInstanceId = createdService.get(j).toString();
                System.out.println("service instance id ::: " + serviceInstanceId);
                serviceService.createBindService(appGuid, serviceInstanceId);
            }
        }
    }

    private ApplicationEntity getAppStats(String appGuid, String appName) throws PlatformException {
        ApplicationEntity application = null;
        int tryCount = 0;
        appService.timer(10);

        // 앱 스타뚜가 여기서 되어야하는군!!!!
        Map result = appService.procStartApplication(appGuid);
        System.out.println("result ::: " + result.toString());

        while(tryCount < 11) {
            appService.timer(30);
            application = appService.getApplicationNameExists(appName);
            tryCount++;
            if(tryCount == 10 && !application.getPackageState().equals("STAGED")) { //5분
                throw new PlatformException("앱이 시작되지 않네요...! 시작중일지도 모르지만용");
            }
            if(application.getPackageState().equals("STAGED")) {
                return application;
            }
        }
        return application;
    }

}
