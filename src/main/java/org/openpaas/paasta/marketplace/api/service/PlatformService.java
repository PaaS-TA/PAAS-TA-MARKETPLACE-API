package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.NameType;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
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

    private final SoftwarePlanService softwarePlanService;

    public String provision(Instance instance, boolean isTested) throws PlatformException {
        if(instance == null) {
            log.info("아직 없어");
            return null;
        }
        String appGuid;

        try {
            Software software = instance.getSoftware();

            String planId = instance.getSoftwarePlanId();
            SoftwarePlan softwarePlan = softwarePlanService.getSoftwarePlan(planId);

            String memorySize = softwarePlan.getMemorySize();
            String diskSize = softwarePlan.getDiskSize();
            String name;

            if(isTested) {
                name = generateName(instance, instance.getAppName());
            } else {
                name = generateName(instance, null);
            }

            // 1) 앱 생성하는 CF 호출(처음에는 app no start)
            Map<String, Object> result = appService.createApp(software, name, memorySize, diskSize);
            if(result.get("appId") == null) {
                throw new PlatformException("Provisioning fail.\n" + result.get("msg"));
            }

            appGuid = result.get("appId").toString();


            // 환경변수 업데이트
            Map env = new HashMap();
            if(result.get("env") != null) {
                log.info("매니페스트에 env 있음!!!!!!!!!!");
                env = (Map) result.get("env");

                int tryCnt = 0;
                boolean isUpdated = false;
                while(tryCnt++ < 10 && !isUpdated) {
                    Map res = appService.updateApp(env, appGuid);
                    isUpdated = (boolean) res.get("result");
                    if(!isUpdated) {
                        Thread.sleep(1000);
                    }

                    // todo :: to delete
                    log.info("env count ::: " + tryCnt);
                }
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

        }catch(PlatformException pe) {
            // todo ::: to delete
            //pe.printStackTrace();
            throw pe;
        } catch (Exception e) {
            // todo ::: to delete
            //e.printStackTrace();
            throw new PlatformException(e);
        }
        return appGuid;
    }

    public void deprovision(Instance instance) throws PlatformException {
        if(instance.getAppGuid() == null) {
            log.info("여기는 앱 안 만들어졌어");
            return;
        }

        try {
            log.info("deprovision startuuu!!!");
            GetApplicationResponse getAppRes = appService.getApp(instance);
            if (getAppRes == null) {
                throw new PlatformException("appGuid not found yet.\n");
            }

            String appGuid = getAppRes.getMetadata().getId();
            if (appGuid == null) {
                throw new PlatformException("appGuid not found yet.\n");
            }

            log.info("어풀리케이쇼온~~~ " + appGuid);


            // 1) appGuid 로 서비스 바인딩 id 모두 찾는다.
            ListApplicationServiceBindingsResponse bindingList = null;
            
            int tryCnt = 0;
            boolean isSuccess = false;
            
            while(tryCnt++ < 10 && !isSuccess) {
            	isSuccess = true;
            	try{
            		bindingList = serviceService.getServiceBindings(appGuid);
            	}catch(Exception e) {
            		isSuccess = false;
            		log.info("bindingList try cound ::: {}", tryCnt);
            		//e.printStackTrace();
            		Thread.sleep(1000);
            	}
            	

            }
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

        }catch(ClientV2Exception cv2e) {
            if("100004".equals(cv2e.getCode()) || "100004".equals(cv2e.getErrorCode()) || cv2e.getMessage().indexOf("CF-AppNotFound") > -1 || cv2e.getDescription().indexOf("CF-AppNotFound") > -1) {
                throw new PlatformException("noCfAppInstance",cv2e);
            }
            log.error(cv2e.getMessage(), cv2e);
            throw new PlatformException(cv2e);
        }catch (Throwable t) {
            log.error(t.getMessage(), t);
            throw new PlatformException(t);
        }

    }


    String generateName(Instance instance, String testPrefix) {
        return localNamingType.generateName(instance, testPrefix);
    }




    private void procServiceBinding(Instance instance, Map env, String appGuid) throws PlatformException, InterruptedException {
        List<String> services = (List) env.get("services");
        log.info("services ::: " + services.toString());

        // 플랜 아이디로 서비스 생성
        int index = 0;

        // services 에 서비스 타입 판별 후 해당하는 서비스 브로커 존재 여부 -> 있으면 서비스 플랜 아이디 조회
        ListServiceBrokersResponse brokers = null;
        int tryCnt = 0;
        boolean isFound = false;
        while(tryCnt++ < 10 && !isFound && brokers == null) {
            isFound = true;
            try {
                brokers = serviceService.getServiceBrokers();
            }catch(Exception npe) {
                isFound = false;

                // todo ::: to delete
                log.info("broker list try count ::: " + tryCnt);
                //npe.printStackTrace();
                Thread.sleep(1000);
            }

        }
        if(brokers == null) {
            throw new PlatformException("Get Service Broker Failed.");
        }

        ServiceBrokerResource broker = null;
        Map<Integer, List> planMap = new HashMap();

        for (String serviceName : services) {
            index++;
            for(ServiceBrokerResource resource : brokers.getResources()){
                List<String> planIdList = new ArrayList<>();
                broker = resource;

                if(broker.getEntity().getName().toLowerCase().contains(serviceName.toLowerCase())){
                    log.info("브로커 아이디이이이 ::: " + broker.getMetadata().getId());
                    //log.info("서비스 플랜들..." + serviceService.getServicePlans(broker.getMetadata().getId()).getResources().toString());

                    ListServicePlansResponse planList = null;
                    tryCnt = 0;
                    isFound = false;
                    while(tryCnt++ < 10 && !isFound) {
                    	try {
                    		planList = serviceService.getServicePlans(broker.getMetadata().getId());
                    		isFound = true;
                    	}catch(Exception e) {
                    		isFound = false;
                    		//e.printStackTrace();
                    		Thread.sleep(1000);
                    	}
                    }
                    	
                    if(planList == null) {
                        throw new PlatformException("Get Service Plan Failed.");
                    }

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
                log.info("service instance id ::: " + planId);
                String serviceName = String.format("service-%d-%d", instance.getId(), i);

                String svcInstanceId = null;
                
                tryCnt = 0;
                isFound = false;
                while(tryCnt++ < 10 && !isFound) {
                	try {
                		svcInstanceId = serviceService.createServiceInstance(serviceName, appGuid, planId);
                		isFound = true;
                	}catch(Exception e) {
                		isFound = false;
                		//e.printStackTrace();
                		Thread.sleep(1000);
                	}
                }
                createdService.add(i, svcInstanceId);
                
            }
        }

        log.info("서비스 인스턴스 생성!!!");


        // 앱 아이디와 서비스 아이디로 바인딩
        if(createdService.size() > 0){
            log.info("앱 아이디와 서비스 아이디로 바인딩");
            for (int j = 0; j < createdService.size(); j++){
                String serviceInstanceId = createdService.get(j).toString();
                log.info("service instance id ::: " + serviceInstanceId);
                
                tryCnt = 0;
                isFound = false;
                while(tryCnt++ < 10 && !isFound) {
                	try {
                		serviceService.createBindService(appGuid, serviceInstanceId);
                		isFound = true;
                	}catch(Exception e) {
                		isFound = false;
                		//e.printStackTrace();
                		Thread.sleep(1000);
                	}
                }                
            }
        }
    }

    private ApplicationEntity getAppStats(String appGuid, String appName) throws PlatformException, InterruptedException {
        ApplicationEntity application = null;
        int tryCount = 0;
        appService.timer(5);

        log.info("============== 앱 START ================");
        // 앱 스타뚜가 여기서 되어야하는군!!!!
        Map result = appService.procStartApplication(appGuid);
        if(result == null) {
            throw new PlatformException("application no start...");
        }
        log.info("result ::: " + result.toString());

        while(tryCount < 51) {
            appService.timer(5);

            tryCount++;

            int tryCnt = 0;
            boolean isExist = false;
            while(tryCnt++ < 10 && !isExist) {
                isExist = true;
                try {
                    application = appService.getApplicationNameExists(appName);
                } catch (Exception e) {
                    // todo :: to delete
                    log.info("app exist count ::: " + tryCnt);

                    isExist = false;
                    Thread.sleep(1000);
                }

            }

            log.info("app state ::: appName=" + appName + ", appState=" + application.getPackageState());
            if(tryCount == 50 && !application.getPackageState().equals("STAGED")) { //3분
                log.info("Not started ::: appName=" + appName + ", appState=" + application.getPackageState());
                throw new PlatformException("앱이 시작되지 않네요...! 시작중일지도 모르지만용");
            }
            if(application.getPackageState().equals("STAGED")) {
                log.info("============== 앱 START END================");
                log.info("TTA [{}] ::: 시간 검증 완료", appName);
                return application;
            }
        }
        log.info("app 을 돌려주세욤" + application);
        return application;
    }


    private void procServiceUnBind(String serviceInstanceId, String appGuid) throws InterruptedException {
        // 2) 언바인드
        Map unbindResult = null;
        
        int tryCnt = 0;
        boolean isFound = false;
        while(tryCnt++ < 10 && !isFound) {
        	try {
        		unbindResult = serviceService.unbindService(serviceInstanceId, appGuid);
        		isFound = true;
        	}catch(Exception e) {
        		isFound = false;
        		//e.printStackTrace();
        		Thread.sleep(1000);
        	}
        }
        log.info("언바인드 ::: " + unbindResult.toString());
        
        // 3) 서비스 인스턴스 삭제
        Map deleteResult = null;
        
        tryCnt = 0;
        isFound = false;
        while(tryCnt++ < 10 && !isFound) {
        	try {
        		deleteResult = serviceService.deleteInstance(serviceInstanceId);
        		isFound = true;
        	}catch(Exception e) {
        		isFound = false;
        		//e.printStackTrace();
        		Thread.sleep(1000);
        	}
        }        
        log.info("서비스 인스턴스 ::: " + deleteResult.toString());
    }
}
