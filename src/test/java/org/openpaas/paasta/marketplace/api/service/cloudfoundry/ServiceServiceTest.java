package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Map;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.servicebindings.CreateServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.DeleteServiceBindingResponse;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingsV2;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokers;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.DeleteServiceInstanceResponse;
import org.cloudfoundry.client.v2.serviceinstances.ListServiceInstanceServiceBindingsResponse;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstances;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlans;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class ServiceServiceTest {

    @Mock
    ServiceService serviceService;

    @Mock
    ReactorCloudFoundryClient cloudFoundryClient;

    @Mock
    ServiceBrokers serviceBrokers;

    @Mock
    ServicePlans servicePlans;

    @Mock
    ServiceInstances serviceInstances;

    @Mock
    ServiceBindingsV2 serviceBindingsV2;

    @Mock
    ApplicationsV2 applicationsV2;

    @Mock
    Mono<ListServiceBrokersResponse> listServiceBrokersResponseMono;

    @Mock
    Mono<CreateServiceInstanceResponse> createServiceInstanceResponseMono;

    @Mock
    Mono<ListServicePlansResponse> listServicePlansResponseMono;

    @Mock
    Mono<CreateServiceBindingResponse> createServiceBindingResponseMono;

    @Mock
    Mono<ListApplicationServiceBindingsResponse> listApplicationServiceBindingsResponseMono;

    @Mock
    Mono<ListServiceInstanceServiceBindingsResponse> listServiceInstanceServiceBindingsResponseMono;

    @Mock
    Mono<DeleteServiceBindingResponse> deleteServiceBindingResponseMono;

    @Mock
    Mono<DeleteServiceInstanceResponse> deleteServiceInstanceResponseMono;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(serviceService, "marketSpaceGuid", "x");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void constructor() {
        serviceService = new ServiceService();
    }

    @Test
    public void getServiceBrokers() {
        given(serviceService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.serviceBrokers()).willReturn(serviceBrokers);
        given(serviceBrokers.list(any())).willReturn(listServiceBrokersResponseMono);
        ListServiceBrokersResponse listServiceBrokersResponse = ListServiceBrokersResponse.builder().build();
        given(listServiceBrokersResponseMono.block()).willReturn(listServiceBrokersResponse);

        given(serviceService.getServiceBrokers()).willCallRealMethod();

        ListServiceBrokersResponse result = serviceService.getServiceBrokers();
        assertEquals(listServiceBrokersResponse, result);
    }

    @Test
    public void getServicePlans() {
        given(serviceService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.servicePlans()).willReturn(servicePlans);
        given(servicePlans.list(any())).willReturn(listServicePlansResponseMono);
        ListServicePlansResponse listServicePlansResponse = ListServicePlansResponse.builder().build();
        given(listServicePlansResponseMono.block()).willReturn(listServicePlansResponse);

        given(serviceService.getServicePlans(any())).willCallRealMethod();

        ListServicePlansResponse result = serviceService.getServicePlans("x");
        assertEquals(listServicePlansResponse, result);
    }

    @Test
    public void createServiceInstance() {
        given(serviceService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.serviceInstances()).willReturn(serviceInstances);
        given(serviceInstances.create(any())).willReturn(createServiceInstanceResponseMono);
        Metadata metadata = Metadata.builder().id("1234").build();
        CreateServiceInstanceResponse createServiceInstanceResponse = CreateServiceInstanceResponse.builder()
                .metadata(metadata).build();
        given(createServiceInstanceResponseMono.block()).willReturn(createServiceInstanceResponse);

        given(serviceService.createServiceInstance(any(), any(), any())).willCallRealMethod();

        String result = serviceService.createServiceInstance("x", "x", "x");
        assertEquals("1234", result);
    }

    @Test
    public void createBindService() {
        given(serviceService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.serviceBindingsV2()).willReturn(serviceBindingsV2);
        given(serviceBindingsV2.create(any())).willReturn(createServiceBindingResponseMono);
        CreateServiceBindingResponse createServiceBindingResponse = CreateServiceBindingResponse.builder().build();
        given(createServiceBindingResponseMono.block()).willReturn(createServiceBindingResponse);

        given(serviceService.createBindService(any(), any())).willCallRealMethod();

        CreateServiceBindingResponse result = serviceService.createBindService("x", "x");
        assertEquals(createServiceBindingResponse, result);
    }

    @Test
    public void getServiceBindings() {
        given(serviceService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.listServiceBindings(any())).willReturn(listApplicationServiceBindingsResponseMono);
        ListApplicationServiceBindingsResponse listApplicationServiceBindingsResponse = ListApplicationServiceBindingsResponse
                .builder().build();
        given(listApplicationServiceBindingsResponseMono.block()).willReturn(listApplicationServiceBindingsResponse);

        given(serviceService.getServiceBindings(any())).willCallRealMethod();

        ListApplicationServiceBindingsResponse result = serviceService.getServiceBindings("x");
        assertEquals(listApplicationServiceBindingsResponse, result);
    }

    @Test
    public void unbindService() {
        given(serviceService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.serviceInstances()).willReturn(serviceInstances);
        given(serviceInstances.listServiceBindings(any())).willReturn(listServiceInstanceServiceBindingsResponseMono);
        Metadata metadata = Metadata.builder().id("1234").build();
        ServiceBindingResource serviceBindingResource = ServiceBindingResource.builder().metadata(metadata).build();
        ListServiceInstanceServiceBindingsResponse listServiceInstanceServiceBindingsResponse = ListServiceInstanceServiceBindingsResponse
                .builder().resource(serviceBindingResource).build();
        given(listServiceInstanceServiceBindingsResponseMono.block())
                .willReturn(listServiceInstanceServiceBindingsResponse);

        given(cloudFoundryClient.serviceBindingsV2()).willReturn(serviceBindingsV2);
        given(serviceBindingsV2.delete(any())).willReturn(deleteServiceBindingResponseMono);
        DeleteServiceBindingResponse deleteServiceBindingResponse = DeleteServiceBindingResponse.builder().build();
        given(deleteServiceBindingResponseMono.block()).willReturn(deleteServiceBindingResponse);

        given(serviceService.unbindService(any(), any())).willCallRealMethod();

        Map<?, ?> result = serviceService.unbindService("x", "x");
        assertEquals(true, result.get("result"));
    }

    @Test
    public void unbindServiceError() {
        given(serviceService.cloudFoundryClient(any())).willThrow(new RuntimeException());

        given(serviceService.unbindService(any(), any())).willCallRealMethod();

        Map<?, ?> result = serviceService.unbindService("x", "x");
        assertEquals(false, result.get("result"));
    }

    @Test
    public void deleteInstance() {
        given(serviceService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.serviceInstances()).willReturn(serviceInstances);
        given(serviceInstances.delete(any())).willReturn(deleteServiceInstanceResponseMono);
        DeleteServiceInstanceResponse deleteServiceInstanceResponse = DeleteServiceInstanceResponse.builder().build();
        given(deleteServiceInstanceResponseMono.block()).willReturn(deleteServiceInstanceResponse);

        given(serviceService.deleteInstance(any())).willCallRealMethod();

        Map<?, ?> result = serviceService.deleteInstance("x");
        assertEquals(true, result.get("result"));
    }

    @Test
    public void deleteInstanceError() {
        given(serviceService.cloudFoundryClient(any())).willThrow(new RuntimeException());

        given(serviceService.deleteInstance(any())).willCallRealMethod();

        Map<?, ?> result = serviceService.deleteInstance("x");
        assertEquals(false, result.get("result"));
    }

}
