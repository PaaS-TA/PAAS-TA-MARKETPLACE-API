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

    @Mock
    ClientV2Exception cv2e;

    boolean isTested;
    boolean getSoftwarePlan;
    boolean createApp;
    boolean getApp;
    boolean getAppAppGuid;
    boolean servicesEmpty;
    boolean envNull;

    boolean getServiceBrokersNull;
    boolean getServicePlansNull;
    boolean procStartApplicationNull;
    boolean getServiceBindingsEmpty;
    boolean getAppClientV2Exception;

    String cv2eErrorCode;
    String cv2eMessage;
    String cv2eDescription;

    boolean updateAppRetry;
    boolean getServiceBrokersRetry;
    boolean getServicePlansRetry;
    boolean createServiceInstanceRetry;
    boolean createBindServiceRetry;
    boolean getServiceBindingsRetry;
    boolean getApplicationNameExistsRetry;
    boolean unbindServiceRetry;
    boolean deleteInstanceRetry;

    boolean getApplicationNameExistsNotStaged;

    String packageState;

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
        servicesEmpty = false;
        envNull = false;

        getServiceBrokersNull = false;
        getServicePlansNull = false;
        procStartApplicationNull = false;
        getServiceBindingsEmpty = false;
        getAppClientV2Exception = false;

        cv2eErrorCode = null;
        cv2eMessage = "";
        cv2eDescription = "";

        updateAppRetry = false;
        getServiceBrokersRetry = false;
        getServicePlansRetry = false;
        createServiceInstanceRetry = false;
        createBindServiceRetry = false;
        getServiceBindingsRetry = false;
        getApplicationNameExistsRetry = false;
        unbindServiceRetry = false;
        deleteInstanceRetry = false;

        getApplicationNameExistsNotStaged = false;

        packageState = "STAGED";

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
        if (!servicesEmpty) {
            services.add("myservice");
        }
        Map<String, Object> env = new TreeMap<>();
        env.put("services", services);
        if (!envNull) {
            createMap.put("env", env);
        }

        ApplicationEntity applicationEntity = ApplicationEntity.builder().name("x").packageState(packageState).build();
        ApplicationEntity applicationEntityNotStaged = ApplicationEntity.builder().name("x").packageState("UNKNOWN")
                .build();
        ServiceBrokerEntity serviceBrokerEntity1 = ServiceBrokerEntity.builder().name("myservice").build();
        Metadata metadata1 = Metadata.builder().id("x").build();
        ServiceBrokerResource serviceBrokerResource1 = ServiceBrokerResource.builder().metadata(metadata1)
                .entity(serviceBrokerEntity1).build();
        ServiceBrokerEntity serviceBrokerEntity2 = ServiceBrokerEntity.builder().name("yourservice").build();
        Metadata metadata2 = Metadata.builder().id("x").build();
        ServiceBrokerResource serviceBrokerResource2 = ServiceBrokerResource.builder().metadata(metadata2)
                .entity(serviceBrokerEntity2).build();
        ListServiceBrokersResponse listServiceBrokersResponse = ListServiceBrokersResponse.builder()
                .resource(serviceBrokerResource1).resource(serviceBrokerResource2).build();

        ServicePlanEntity servicePlanEntity = ServicePlanEntity.builder().serviceId("x").build();
        ServicePlanResource servicePlanResource = ServicePlanResource.builder().metadata(metadata1)
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
                // FIXME:
                if (mark("getApplicationNameExistsRetry") < 50) {
                    throw new RuntimeException();
                }
                return applicationEntity;
            });
        }
        if (getApplicationNameExistsNotStaged) {
            given(appService.getApplicationNameExists(any(String.class))).willReturn(applicationEntityNotStaged);
        }
        given(serviceService.getServiceBrokers()).willReturn(getServiceBrokersNull ? null : listServiceBrokersResponse);
        if (getServiceBrokersRetry) {
            given(serviceService.getServiceBrokers()).willAnswer(x -> {
                // FIXME:
                if (mark("getServiceBrokersRetry") < 10) {
                    throw new RuntimeException();
                }
                return getServiceBrokersNull ? null : listServiceBrokersResponse;
            });
        }
        given(serviceService.getServicePlans(any(String.class)))
                .willReturn(getServicePlansNull ? null : listServicePlansResponse);
        if (getServicePlansRetry) {
            given(serviceService.getServicePlans(any(String.class))).willAnswer(x -> {
                // FIXME:
                if (mark("getServicePlansRetry") <= 10) {
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
                // FIXME:
                result.put("result", mark("updateAppRetry") > 10 ? true : false);
                return result;
            });
        }
        given(serviceService.createServiceInstance(any(String.class), any(String.class), any(String.class)))
                .willReturn("x");
        if (createServiceInstanceRetry) {
            given(serviceService.createServiceInstance(any(String.class), any(String.class), any(String.class)))
                    .willAnswer(x -> {
                        // FIXME:
                        if (mark("createServiceInstanceRetry") <= 10) {
                            throw new RuntimeException();
                        }
                        return "x";
                    });
        }
        if (createBindServiceRetry) {
            given(serviceService.createBindService(any(String.class), any(String.class))).willAnswer(x -> {
                // FIXME:
                if (mark("createBindServiceRetry") <= 10) {
                    throw new RuntimeException();
                }

                return null;
            });
        }

        String appGuid = platformService.provision(instance1, isTested);
        assertNotNull(appGuid);
    }

    @Test(expected = PlatformException.class)
    public void provisionPackageStateError() throws PlatformException {
        packageState = "UNKNOWN";

        provision();
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

    @Test
    public void provisionUpdateAppRetry() throws PlatformException {
        updateAppRetry = true;

        provision();
    }

    @Test
    public void provisionGetServiceBrokersRetry() throws PlatformException {
        getServiceBrokersRetry = true;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionGetServiceBrokersRetryNull() throws PlatformException {
        getServiceBrokersNull = true;
        getServiceBrokersRetry = true;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionGetServicePlansRetry() throws PlatformException {
        getServicePlansRetry = true;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionCreateServiceInstanceRetry() throws PlatformException {
        createServiceInstanceRetry = true;

        provision();
    }

    @Test
    public void provisionCreateBindServiceRetry() throws PlatformException {
        createBindServiceRetry = true;

        provision();
    }

    @Test
    public void provisionGetApplicationNameExistsRetry() throws PlatformException {
        getApplicationNameExistsRetry = true;

        provision();
    }

    @Test(expected = PlatformException.class)
    public void provisionGetApplicationNameExistsNotStaged() throws PlatformException {
        getApplicationNameExistsNotStaged = true;

        provision();
    }

    @Test
    public void provisionServicesEmpty() throws PlatformException {
        servicesEmpty = true;

        provision();
    }

    @Test
    public void provisionEnvNully() throws PlatformException {
        envNull = true;

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
        if (getServiceBindingsEmpty) {
            listApplicationServiceBindingsResponse = ListApplicationServiceBindingsResponse.builder().totalResults(0)
                    .build();
        }

        RouteMappingEntity routeMappingEntity = RouteMappingEntity.builder().routeId("x").build();
        RouteMappingResource routeMappingResource = RouteMappingResource.builder().entity(routeMappingEntity).build();
        ListRouteMappingsResponse listRouteMappingsResponse = ListRouteMappingsResponse.builder()
                .resource(routeMappingResource).build();

        if (getApp) {
            given(appService.getApp(any(Instance.class))).willReturn(getApplicationResponse);
        }
        if (getAppClientV2Exception) {
            given(appService.getApp(any(Instance.class))).willThrow(cv2e);
            given(cv2e.getErrorCode()).willReturn(cv2eErrorCode);
            given(cv2e.getMessage()).willReturn(cv2eMessage);
            given(cv2e.getDescription()).willReturn(cv2eDescription);
        }
        given(serviceService.getServiceBindings(any(String.class))).willReturn(listApplicationServiceBindingsResponse);
        if (getServiceBindingsRetry) {
            given(serviceService.getServiceBindings(any(String.class))).willAnswer(x -> {
                // FIXME:
                if (mark("getServiceBindingsRetry") <= 10) {
                    throw new RuntimeException();
                }
                return ListApplicationServiceBindingsResponse.builder().resource(serviceBindingResource).totalResults(1)
                        .build();
            });
        }
        given(appService.getRouteMappingList(any(String.class))).willReturn(listRouteMappingsResponse);
        given(serviceService.unbindService(any(String.class), any(String.class)))
                .willReturn(new HashMap<String, Object>());
        if (unbindServiceRetry) {
            given(serviceService.unbindService(any(String.class), any(String.class))).willAnswer(x -> {
                // FIXME:
                if (mark("unbindServiceRetry") <= 10) {
                    throw new RuntimeException();
                }
                return new HashMap<Object, Object>();
            });
        }
        given(serviceService.deleteInstance(any(String.class))).willReturn(new HashMap<String, Object>());
        if (deleteInstanceRetry) {
            given(serviceService.deleteInstance(any(String.class))).willAnswer(x -> {
                // FIXME:
                if (mark("deleteInstanceRetry") <= 10) {
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
    public void deprovisionGetAppClientV2ExceptionError1() throws PlatformException {
        getAppClientV2Exception = true;
        cv2eErrorCode = "100004";

        deprovision();
    }

    @Test(expected = PlatformException.class)
    public void deprovisionGetAppClientV2ExceptionError2() throws PlatformException {
        getAppClientV2Exception = true;
        cv2eMessage = "CF-AppNotFound";

        deprovision();
    }

    @Test(expected = PlatformException.class)
    public void deprovisionGetAppClientV2ExceptionError3() throws PlatformException {
        getAppClientV2Exception = true;
        cv2eDescription = "CF-AppNotFound";

        deprovision();
    }

    @Test(expected = PlatformException.class)
    public void deprovisionGetServiceBindingsRetry() throws PlatformException {
        getServiceBindingsRetry = true;

        deprovision();
    }

    @Test(expected = PlatformException.class)
    public void deprovisionUnbindServiceRetry() throws PlatformException {
        unbindServiceRetry = true;

        deprovision();
    }

    @Test(expected = PlatformException.class)
    public void deprovisionDeleteInstanceRetry() throws PlatformException {
        deleteInstanceRetry = true;

        deprovision();
    }

    @Test
    public void deprovisionGetServiceBindingsEmpty() throws PlatformException {
        getServiceBindingsEmpty = true;

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
