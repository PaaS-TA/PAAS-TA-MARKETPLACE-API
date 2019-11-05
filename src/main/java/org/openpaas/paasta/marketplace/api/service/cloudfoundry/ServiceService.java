package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsRequest;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.applications.RemoveApplicationRouteRequest;
import org.cloudfoundry.client.v2.routes.DeleteRouteRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.serviceinstances.*;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.openpaas.paasta.marketplace.api.config.common.Common;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-27
 */
@Service
public class ServiceService extends Common {
    @Value("${market.space.guid}")
    public String marketSpaceGuid;

    public ListServiceBrokersResponse getServiceBrokers() {
        return cloudFoundryClient(tokenProvider()).serviceBrokers().list(ListServiceBrokersRequest.builder().build()).block();
    }

    public ListServicePlansResponse getServicePlans(String serviceBrokerId) {
        return cloudFoundryClient(tokenProvider()).servicePlans().list(ListServicePlansRequest.builder().serviceBrokerId(serviceBrokerId).build()).block();
    }

    public String createServiceInstance(String  serviceName, String appGuid, String planGuid) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("app_guid", appGuid);

        CreateServiceInstanceResponse createserviceinstanceresponse = cloudFoundryClient(tokenProvider()).serviceInstances()
                .create(CreateServiceInstanceRequest.builder()
                        .name(serviceName)
                        .spaceId(marketSpaceGuid)
                        .parameters(parameterMap)
                        .servicePlanId(planGuid)
                        .build()).block();

        return createserviceinstanceresponse.getMetadata().getId();

    }

    public CreateServiceBindingResponse createBindService(String appGuid, String serviceInstanceId) {
        return cloudFoundryClient(tokenProvider()).serviceBindingsV2()
                .create(CreateServiceBindingRequest.builder()
                        .applicationId(appGuid)
                        .serviceInstanceId(serviceInstanceId).build())
                .block();
    }

    public ListApplicationServiceBindingsResponse getServiceBindings(String appGuid) {
        return cloudFoundryClient(tokenProvider()).applicationsV2().listServiceBindings(ListApplicationServiceBindingsRequest.builder().applicationId(appGuid).build()).block();
    }

    

    /**
     * 앱-서비스를 언바인드한다.
     *
     * @param serviceInstanceId
     * @param applicationId
     * @return
     */
    public Map unbindService(String serviceInstanceId, String applicationId) {
        Map resultMap = new HashMap();

        try {
            ListServiceInstanceServiceBindingsResponse listServiceInstanceServiceBindingsResponse = cloudFoundryClient(tokenProvider()).serviceInstances().listServiceBindings(ListServiceInstanceServiceBindingsRequest.builder().applicationId(applicationId).serviceInstanceId(serviceInstanceId).build()).block();
            String instancesServiceBindingGuid = listServiceInstanceServiceBindingsResponse.getResources().get(0).getMetadata().getId();

            DeleteServiceBindingResponse deleteServiceBindingResponse = cloudFoundryClient(tokenProvider()).serviceBindingsV2().delete(DeleteServiceBindingRequest.builder().serviceBindingId(instancesServiceBindingGuid).build()).block();

            resultMap.put("result", true);

        } catch (Exception e) {
            //e.printStackTrace();
            resultMap.put("result", false);
            resultMap.put("msg", e);
        }

        return resultMap;
    }


    /**
     * 서비스 인스턴스를 삭제한다.
     *
     * @param guid
     * @return
     */
    public Map deleteInstance(String guid) {
        HashMap result = new HashMap();
        try {
            cloudFoundryClient(tokenProvider()).serviceInstances().delete(DeleteServiceInstanceRequest.builder().serviceInstanceId(guid).build()).block();
            result.put("result", true);
            result.put("msg", "You have successfully completed the task.");
        } catch (Exception e) {
            result.put("result", false);
            result.put("msg", e.getMessage());
        }
        return result;
    }


}
