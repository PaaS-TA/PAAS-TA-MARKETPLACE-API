package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cloudfoundry.client.v2.ClientV2Exception;
import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationServiceBindingsResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routemappings.RouteMappingEntity;
import org.cloudfoundry.client.v2.routemappings.RouteMappingResource;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingEntity;
import org.cloudfoundry.client.v2.servicebindings.ServiceBindingResource;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerEntity;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansResponse;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanEntity;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.NameType;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
import org.openpaas.paasta.marketplace.api.exception.PlatformException;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.AppService;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.ServiceService;
import org.springframework.test.util.ReflectionTestUtils;

public class PlatformServiceTest extends AbstractMockTest {

    PlatformService platformService;

    @Mock
    AppService appService;

    @Mock
    ServiceService serviceService;

    @Mock
    SoftwarePlanService softwarePlanService;

    boolean isTested;
    boolean getSoftwarePlan;
    boolean createApp;
    boolean getApp;
    boolean getAppAppGuid;

    boolean getServiceBrokersNull;
    boolean getServicePlansNull;
    boolean procStartApplicationNull;
    boolean getAppClientV2Exception;
    String errorCode;

    boolean updateAppRetry;
    boolean getServiceBrokersRetry;
    boolean getServicePlansRetry;
    boolean createServiceInstanceRetry;
    boolean createBindServiceRetry;
    boolean getServiceBindingsRetry;
    boolean getApplicationNameExistsRetry;
    boolean unbindServiceRetry;
    boolean deleteInstanceRetry;

    Map<String, Integer> marks;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        platformService = new PlatformService(appService, serviceService, softwarePlanService);

        ReflectionTestUtils.setField(platformService, "localNamingType", NameType.Auto);

        isTested = true;
        getSoftwarePlan = true;
        createApp = true;
        getApp = true;
        getAppAppGuid = true;

        getServiceBrokersNull = false;
        getServicePlansNull = false;
        procStartApplicationNull = false;
        getAppClientV2Exception = false;
        errorCode = "x";

        updateAppRetry = false;
        getServiceBrokersRetry = false;
        getServicePlansRetry = false;
        createServiceInstanceRetry = false;
        createBindServiceRetry = false;
        getServiceBindingsRetry = false;
        getApplicationNameExistsRetry = false;
        unbindServiceRetry = false;
        deleteInstanceRetry = false;

        marks = new TreeMap<>();
    }

    @Test
    public void provision() throws PlatformException {
        SoftwarePlan softwarePlan1 = softwarePlan(1L, 1L);
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        instance1.setSoftwarePlanId("1");

        Map<String, Object> createMap = new TreeMap<>();
        createMap.put("appId", "x");
        List<String> services = new ArrayList<>();
        services.add("myservice");
        Map<String, Object> env = new TreeMap<>();
        env.put("services", services);
        createMap.put("env", env);

        ApplicationEntity applicationEntity = ApplicationEntity.builder().name("x").packageState("STAGED").build();
        ServiceBrokerEntity serviceBrokerEntity = ServiceBrokerEntity.builder().name("myservice").build();
        Metadata metadata = Metadata.builder().id("x").build();
        ServiceBrokerResource serviceBrokerResource = ServiceBrokerResource.builder().metadata(metadata)
                .entity(serviceBrokerEntity).build();
        ListServiceBrokersResponse listServiceBrokersResponse = ListServiceBrokersResponse.builder()
                .resource(serviceBrokerResource).build();
        ServicePlanEntity servicePlanEntity = ServicePlanEntity.builder().serviceId("x").build();
        ServicePlanResource servicePlanResource = ServicePlanResource.builder().metadata(metadata)
                .entity(servicePlanEntity).build();
        ListServicePlansResponse listServicePlansResponse = ListServicePlansResponse.builder()
                .resource(servicePlanResource).build();

        if (getSoftwarePlan) {
            given(softwarePlanService.getSoftwarePlan(any(String.class))).willReturn(softwarePlan1);
        }
        if (createApp) {
            given(appService.createApp(any(Software.class), any(String.class), any(String.class), any(String.class)))
                    .willReturn(createMap);
        }
        Map<String, Object> procStartApplicationResult = new TreeMap<>();
        given(appService.procStartApplication(any(String.class)))
                .willReturn(procStartApplicationNull ? null : procStartApplicationResult);
        given(appService.getApplicationNameExists(any(String.class))).willReturn(applicationEntity);
        if (getApplicationNameExistsRetry) {
            given(appService.getApplicationNameExists(any(String.class))).willAnswer(x -> {
                if (mark("getApplicationNameExistsRetry") <= 1) {
                    throw new RuntimeException();
                }
                return applicationEntity;
            });
        }
        given(serviceService.getServiceBrokers()).willReturn(getServiceBrokersNull ? null : listServiceBrokersResponse);
        if (getServiceBrokersRetry) {
            given(serviceService.getServiceBrokers()).willAnswer(x -> {
                if (mark("getServiceBrokersRetry") <= 1) {
                    throw new RuntimeException();
                }
                return listServiceBrokersResponse;
            });
        }
        given(serviceService.getServicePlans(any(String.class)))
                .willReturn(getServicePlansNull ? null : listServicePlansResponse);
        if (getServicePlansRetry) {
            given(serviceService.getServicePlans(any(String.class))).willAnswer(x -> {
                if (mark("getServicePlansRetry") <= 1) {
                    throw new RuntimeException();
                }
                return listServicePlansResponse;
            });
        }
        Map<String, Object> updateAppResult = new TreeMap<String, Object>();
        updateAppResult.put("result", true);
        given(appService.updateApp(any(Map.class), any(String.class))).willReturn(updateAppResult);
        if (updateAppRetry) {
            given(appService.updateApp(any(Map.class), any(String.class))).willAnswer(x -> {
                Map<String, Object> result = new TreeMap<String, Object>();
                result.put("result", mark("updateAppRetry") > 1 ? true : false);
                return result;
            });
        }
        given(serviceService.createServiceInstance(any(String.class), any(String.class), any(String.class)))
                .willReturn("x");
        if (createServiceInstanceRetry) {
            given(serviceService.createServiceInstance(any(String.class), any(String.class), any(String.class)))
                    .willAnswer(x -> {
                        if (mark("createServiceInstanceRetry") <= 1) {
                            throw new RuntimeException();
                        }
                        return "x";
                    });
        }
        if (createBindServiceRetry) {
            given(serviceService.createBindService(any(String.class), any(String.class))).willAnswer(x -> {
                if (mark("createBindServiceRetry") <= 1) {
                    throw new RuntimeException();
                }

                return null;
            });
        }

        String appGuid = platformService.provision(instance1, isTested);
        assertNotNull(appGuid);
    }

    @Test
    public void provisionIsTestedFalse() throws PlatformException {
        isTested = false;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionGetSoftwarePlanAppError() throws PlatformException {
        getSoftwarePlan = false;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionCreateAppError() throws PlatformException {
        createApp = false;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionGetServiceBrokersNull() throws PlatformException {
        getServiceBrokersNull = true;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionGetServicePlansNull() throws PlatformException {
        getServicePlansNull = true;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionProcStartApplicationNull() throws PlatformException {
        procStartApplicationNull = true;

        provision();
    }

    @Test
    public void provisionWithoutInstance() throws PlatformException {
        String appGuid = platformService.provision(null, false);
        assertNull(appGuid);
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void provisionUpdateAppRetry() throws PlatformException {
        updateAppRetry = true;

        provision();
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void provisionGetServiceBrokersRetry() throws PlatformException {
        getServiceBrokersRetry = true;

        provision();
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void provisionGetServicePlansRetry() throws PlatformException {
        getServicePlansRetry = true;

        provision();
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void provisionCreateServiceInstanceRetry() throws PlatformException {
        createServiceInstanceRetry = true;

        provision();
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void provisionCreateBindServiceRetry() throws PlatformException {
        createBindServiceRetry = true;

        provision();
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void provisionGetApplicationNameExistsRetry() throws PlatformException {
        getApplicationNameExistsRetry = true;

        provision();
    }

    @Test
    public void deprovision() throws PlatformException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        instance1.setSoftwarePlanId("1");
        instance1.setAppGuid("x");

        Map<String, Object> createMap = new TreeMap<>();
        createMap.put("appId", "x");

        Metadata.Builder metadataBuilder = Metadata.builder();
        if (getAppAppGuid) {
            metadataBuilder.id("x");
        }
        Metadata metadata = metadataBuilder.build();
        GetApplicationResponse getApplicationResponse = GetApplicationResponse.builder().metadata(metadata).build();
        ServiceBindingEntity serviceBindingEntity = ServiceBindingEntity.builder().serviceInstanceId("x").build();
        ServiceBindingResource serviceBindingResource = ServiceBindingResource.builder().entity(serviceBindingEntity)
                .build();
        ListApplicationServiceBindingsResponse listApplicationServiceBindingsResponse = ListApplicationServiceBindingsResponse
                .builder().resource(serviceBindingResource).totalResults(1).build();
        RouteMappingEntity routeMappingEntity = RouteMappingEntity.builder().routeId("x").build();
        RouteMappingResource routeMappingResource = RouteMappingResource.builder().entity(routeMappingEntity).build();
        ListRouteMappingsResponse listRouteMappingsResponse = ListRouteMappingsResponse.builder()
                .resource(routeMappingResource).build();

        if (getApp) {
            given(appService.getApp(any(Instance.class))).willReturn(getApplicationResponse);
        }
        if (getAppClientV2Exception) {
            given(appService.getApp(any(Instance.class))).willThrow(new ClientV2Exception(1, 1, "x", errorCode));
        }
        given(serviceService.getServiceBindings(any(String.class))).willReturn(listApplicationServiceBindingsResponse);
        if (getServiceBindingsRetry) {
            given(serviceService.getServiceBindings(any(String.class))).willAnswer(x -> {
                if (mark("getServiceBindingsRetry") <= 1) {
                    throw new RuntimeException();
                }
                return listApplicationServiceBindingsResponse;
            });
        }
        given(appService.getRouteMappingList(any(String.class))).willReturn(listRouteMappingsResponse);
        given(serviceService.unbindService(any(String.class), any(String.class)))
                .willReturn(new HashMap<String, Object>());
        if (unbindServiceRetry) {
            given(serviceService.unbindService(any(String.class), any(String.class))).willAnswer(x -> {
                if (mark("unbindServiceRetry") <= 1) {
                    throw new RuntimeException();
                }
                return new HashMap<Object, Object>();
            });
        }
        given(serviceService.deleteInstance(any(String.class))).willReturn(new HashMap<String, Object>());
        if (deleteInstanceRetry) {
            given(serviceService.deleteInstance(any(String.class))).willAnswer(x -> {
                if (mark("deleteInstanceRetry") <= 1) {
                    throw new RuntimeException();
                }
                return new HashMap<Object, Object>();
            });
        }

        platformService.deprovision(instance1);
    }

    @Test
    public void deprovisionWithoutAppGuid() throws PlatformException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        instance1.setSoftwarePlanId("1");

        platformService.deprovision(instance1);
    }

    @Test(expected = PlatformException.class)
    public void deprovisionGetAppError() throws PlatformException {
        getApp = false;

        deprovision();
    }

    @Test(expected = PlatformException.class)
    public void deprovisionGetAppGuidError() throws PlatformException {
        getAppAppGuid = false;

        deprovision();
    }

    @Test(expected = PlatformException.class)
    public void deprovisionGetAppClientV2Exception() throws PlatformException {
        getAppClientV2Exception = true;

        deprovision();
    }

    @Test(expected = PlatformException.class)
    public void deprovisionGetAppClientV2ExceptionErrorCode() throws PlatformException {
        getAppClientV2Exception = true;
        errorCode = "100004";

        deprovision();
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void deprovisionGetServiceBindingsRetry() throws PlatformException {
        getServiceBindingsRetry = true;

        deprovision();
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void deprovisionUnbindServiceRetry() throws PlatformException {
        unbindServiceRetry = true;

        deprovision();
    }

    /**
     * Test time > 1,000ms
     */
    @Test
    public void deprovisionDeleteInstanceRetry() throws PlatformException {
        deleteInstanceRetry = true;

        deprovision();
    }

    public int mark(String key) {
        Integer value = marks.get(key);
        if (value == null) {
            marks.put(key, 1);
        } else {
            marks.put(key, ++value);
        }

        return marks.get(key);
    }

}
