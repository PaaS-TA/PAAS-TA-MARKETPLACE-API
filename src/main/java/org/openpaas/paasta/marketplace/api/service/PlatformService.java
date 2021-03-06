package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
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

@Slf4j
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
        if(instance == null) {
            log.info("아직 없어");
            return;
        }

        try {
            Software software = instance.getSoftware();
            String name = generateName(instance);

            // 1) 앱 생성하는 CF 호출(처음에는 app no start)
            Map<String, Object> result = appService.createApp(software, name);
            String appGuid = result.get("appId").toString();


            // 환경변수 업데이트
            Map env = new HashMap();
            if(result.get("env") != null) {
                log.info("매니페스트에 env 있음!!!!!!!!!!");
                env = (Map) result.get("env");
                appService.updateApp(env, appGuid);
                log.info("env 업뎃 완료!!!!!!!!!!");
            }

            // 2) 나머지 값 채워주기
            instance.setAppGuid(appGuid);
            instance.setAppName(name);
            instance.setAppUrl(name + cfHostName);

            // 3) 서비스 요청이 있을 경우
            if(env.containsKey("services")){
                log.info("서비스 요청 들어옴!!!!!!!!!!");
                procServiceBinding(instance, env, appGuid);
                log.info("서비스 바인딩 완료!!!!!!!!!!");
            }

            // 4) 앱 시작 및 상태 체크
            getAppStats(appGuid, name);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new PlatformException(t);
        }
    }

    public void deprovision(Instance instance) throws PlatformException {
        if(instance.getAppGuid() == null) {
            log.info("여기는 앱 안 만들어졌어");
            return;
        }

        try {
            String appGuid = appService.getApp(instance).getMetadata().getId();
            log.info("어풀리케이쇼온~~~ " + appGuid);


            // 1) appGuid 로 서비스 바인딩 id 모두 찾는다.
            ListApplicationServiceBindingsResponse bindingList = serviceService.getServiceBindings(appGuid);

            if (bindingList.getTotalResults() > 0) {
                for (int i = 0; i < bindingList.getResources().size(); i++) {
                    String serviceInstanceId = bindingList.getResources().get(i).getEntity().getServiceInstanceId();
                    log.info("서비스 인스턴스 아뒤 ::: " + serviceInstanceId);
                    // 2) 언바인드 & 3) 서비스 삭제
                    procServiceUnBind(serviceInstanceId, appGuid);
                }
            }


            // 4) 라우트 삭제
            ListRouteMappingsResponse listRoutesMapping = appService.getRouteMappingList(appGuid);
            log.info("라우트 리스트 ::: " + listRoutesMapping.getResources().toString());

            listRoutesMapping.getResources().forEach(entity -> {
                String routeId = entity.getEntity().getRouteId();
                log.info("라우트 id ::: " + routeId);
                appService.removeApplicationRoute(appGuid, routeId);
            });


            // 5) 앱 삭제
            appService.deleteApp(appGuid);

        }catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new PlatformException(t);
        }

    }


    String generateName(Instance instance) {
        return localNamingType.generateName(instance);
    }




    private void procServiceBinding(Instance instance, Map env, String appGuid){
        List<String> services = (List) env.get("services");
        log.info("services ::: " + services.toString());

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
                    log.info("브로커 아이디이이이 ::: " + broker.getMetadata().getId());
                    log.info("서비스 플랜들..." + serviceService.getServicePlans(broker.getMetadata().getId()).getResources().toString());

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

        log.info("서비스 인스턴스 생성!!!");


        // 앱 아이디와 서비스 아이디로 바인딩
        if(createdService.size() > 0){
            log.info("앱 아이디와 서비스 아이디로 바인딩");
            for (int j = 0; j < createdService.size(); j++){
                String serviceInstanceId = createdService.get(j).toString();
                log.info("service instance id ::: " + serviceInstanceId);
                serviceService.createBindService(appGuid, serviceInstanceId);
            }
        }
    }

    private ApplicationEntity getAppStats(String appGuid, String appName) throws PlatformException {
        ApplicationEntity application = null;
        int tryCount = 0;
        appService.timer(10);

        log.info("============== 앱 START ================");
        // 앱 스타뚜가 여기서 되어야하는군!!!!
        Map result = appService.procStartApplication(appGuid);
        log.info("result ::: " + result.toString());

        while(tryCount < 7) {
            appService.timer(30);
            application = appService.getApplicationNameExists(appName);
            tryCount++;
            System.err.println("app state ::: appName=" + appName + ", appState=" + application.getPackageState());
            if(tryCount == 6 && !application.getPackageState().equals("STAGED")) { //3분
                System.err.println("Not started ::: appName=" + appName + ", appState=" + application.getPackageState());
                throw new PlatformException("앱이 시작되지 않네요...! 시작중일지도 모르지만용");
            }
            if(application.getPackageState().equals("STAGED")) {
                log.info("============== 앱 START END================");
                return application;
            }
        }
        log.info("app 을 돌려주세욤" + application);
        return application;
    }


    private void procServiceUnBind(String serviceInstanceId, String appGuid) {
        // 2) 언바인드
        Map unbindResult = serviceService.unbindService(serviceInstanceId, appGuid);
        log.info("언바인드 ::: " + unbindResult.toString());
        // 3) 서비스 인스턴스 삭제
        Map deleteResult = serviceService.deleteInstance(serviceInstanceId);
        log.info("서비스 인스턴스 ::: " + deleteResult.toString());
    }
}
