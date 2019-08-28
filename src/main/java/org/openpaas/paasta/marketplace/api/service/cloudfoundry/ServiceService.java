package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingRequest;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
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
        return cloudFoundryClient().serviceBrokers().list(ListServiceBrokersRequest.builder().build()).block();
    }

    public ListServicePlansResponse getServicePlans(String serviceBrokerId) {
        return cloudFoundryClient().servicePlans().list(ListServicePlansRequest.builder().serviceBrokerId(serviceBrokerId).build()).block();
    }

    public String createServiceInstance(String  serviceName, String appGuid, String planGuid) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("app_guid", appGuid);

        CreateServiceInstanceResponse createserviceinstanceresponse = cloudFoundryClient().serviceInstances()
                .create(CreateServiceInstanceRequest.builder()
                        .name(serviceName)
                        .spaceId(marketSpaceGuid)
                        .parameters(parameterMap)
                        .servicePlanId(planGuid)
                        .build()).block();

        return createserviceinstanceresponse.getMetadata().getId();

    }

    public CreateServiceBindingResponse createBindService(String appGuid, String serviceInstanceId) {
        return cloudFoundryClient().serviceBindingsV2()
                .create(CreateServiceBindingRequest.builder()
                        .applicationId(appGuid)
                        .serviceInstanceId(serviceInstanceId).build())
                .block();
    }
}
