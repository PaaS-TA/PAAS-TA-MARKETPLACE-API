package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.cloudfoundry.client.v2.Metadata;
import org.cloudfoundry.client.v2.applications.ApplicationEntity;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.CreateApplicationResponse;
import org.cloudfoundry.client.v2.applications.GetApplicationResponse;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.client.v2.applications.UpdateApplicationResponse;
import org.cloudfoundry.client.v2.applications.UploadApplicationResponse;
import org.cloudfoundry.client.v2.routemappings.CreateRouteMappingResponse;
import org.cloudfoundry.client.v2.routemappings.ListRouteMappingsResponse;
import org.cloudfoundry.client.v2.routemappings.RouteMappings;
import org.cloudfoundry.client.v2.routes.CreateRouteResponse;
import org.cloudfoundry.client.v2.routes.DeleteRouteResponse;
import org.cloudfoundry.client.v2.routes.Routes;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.EventType;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.App;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.exception.PlatformException;
import org.springframework.test.util.ReflectionTestUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class AppServiceTest {

    @Mock
    AppService appService;

    @Mock
    Container container;

    @Mock
    StoredObject storedObject;

    @Mock
    ReactorCloudFoundryClient cloudFoundryClient;

    @Mock
    ReactorDopplerClient reactorDopplerClient;

    @Mock
    ApplicationsV2 applicationsV2;

    @Mock
    Routes routes;

    @Mock
    RouteMappings routeMappings;

    @Mock
    Mono<ListApplicationsResponse> listApplicationsResponseMono;

    @Mock
    Mono<CreateApplicationResponse> createApplicationResponseMono;

    @Mock
    Mono<CreateRouteResponse> createRouteResponseMono;

    @Mock
    Mono<CreateRouteMappingResponse> createRouteMappingResponse;

    @Mock
    Mono<UploadApplicationResponse> uploadApplicationResponseMono;

    @Mock
    Mono<UpdateApplicationResponse> updateApplicationResponseMono;

    @Mock
    Mono<GetApplicationResponse> getApplicationResponseMono;

    @Mock
    Mono<ListRouteMappingsResponse> listRouteMappingsResponseMono;

    @Mock
    Mono<Void> removeRouteMono;

    @Mock
    Mono<DeleteRouteResponse> deleteRouteResponseMono;

    @Mock
    Mono<Void> deleteApplicationMono;

    @Mock
    Flux<Envelope> envelopeFlux;

    @Mock
    Mono<List<Envelope>> listEnvelopeMono;

    @Mock
    File file;

    @Mock
    Map<String, String> stringStringMap;

    boolean createApplicationRetry;

    boolean createManifestFileError;

    boolean createRouteError;

    boolean routeMappingError;

    String requestedMemorySize;

    String requestedDiskSize;

    boolean envInstances;

    boolean envBuildpacks;

    boolean envJsonNull;

    boolean envJsonEmpty;

    boolean envJsonSizeMinus;

    String applicationName;

    Map<String, Integer> marks;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(appService, "container", container);
        ReflectionTestUtils.setField(appService, "marketSpaceGuid", "x");
        ReflectionTestUtils.setField(appService, "marketDomainGuid", "x");
        ReflectionTestUtils.setField(appService, "marketOrgGuid", "x");
        ReflectionTestUtils.setField(appService, "marketSpaceGuid", "x");

        createApplicationRetry = false;
        createManifestFileError = false;
        createRouteError = false;
        routeMappingError = false;

        requestedMemorySize = "4G";
        requestedDiskSize = "30G";

        envInstances = true;
        envBuildpacks = true;

        envJsonNull = false;
        envJsonEmpty = false;
        envJsonSizeMinus = false;

        applicationName = "x";

        marks = new TreeMap<>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void constructor() {
        appService = new AppService(container);
    }

    @Test
    public void getAppList() throws IOException {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.list(any())).willReturn(listApplicationsResponseMono);
        ListApplicationsResponse listApplicationsResponse = ListApplicationsResponse.builder().build();
        given(listApplicationsResponseMono.block()).willReturn(listApplicationsResponse);

        given(appService.getAppList(any(), any())).willCallRealMethod();

        ListApplicationsResponse result = appService.getAppList("x", "x");
        assertEquals(listApplicationsResponse, result);
    }

    @Test
    public void createApp() throws Exception {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);

        Map<String, Object> manifestFile = new TreeMap<>();
        List<Object> applications = new ArrayList<>();
        LinkedHashMap<String, Object> env = new LinkedHashMap<>();
        if (envInstances) {
            env.put("instances", 1);
        }
        if (envBuildpacks) {
            List<String> buildpacks = new ArrayList<>();
            buildpacks.add("x");
            env.put("buildpacks", buildpacks);
        }
        applications.add(env);
        manifestFile.put("applications", applications);
        given(appService.createManifestFile(any())).willReturn(manifestFile);
        if (createManifestFileError) {
            given(appService.createManifestFile(any())).willThrow(new RuntimeException());
        }
        given(appService.createApplication(any(), any())).willReturn("x");
        if (createApplicationRetry) {
            given(appService.createApplication(any(), any())).willAnswer(x -> {
                if (mark("createApplicationRetry") <= 1) {
                    throw new NullPointerException();
                }
                return "x";
            });
        }
        given(appService.createRoute(any(), any())).willReturn("x");
        if (createRouteError) {
            given(appService.createRoute(any(), any())).willThrow(new RuntimeException());
        }
        if (routeMappingError) {
            doThrow(new RuntimeException()).when(appService).routeMapping(any(), any(), any());
        }
        given(appService.createTempFile(any())).willReturn(file);
        given(cloudFoundryClient.routes()).willReturn(routes);
        given(routes.delete(any())).willReturn(deleteRouteResponseMono);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.delete(any())).willReturn(deleteApplicationMono);

        given(appService.createApp(any(), any(), any(), any())).willCallRealMethod();

        Software software = new Software();
        Map<String, Object> result = appService.createApp(software, "x", requestedMemorySize, requestedDiskSize);
        if (!createManifestFileError && !routeMappingError && !createRouteError) {
            assertEquals("x", result.get("appId"));
            assertEquals(env, result.get("env"));
        } else {
            assertEquals("fail", result.get("RESULT"));
        }
    }

    @Test
    public void createAppWithoutUnitG() throws Exception {
        requestedMemorySize = String.valueOf(6 * 1024);
        requestedDiskSize = String.valueOf(30 * 1024);

        createApp();
    }

    @Test
    public void createAppWithoutEnvInstancesAndBuildpacks() throws Exception {
        envInstances = false;
        envBuildpacks = false;

        createApp();
    }

    @Test
    public void createAppCreateApplicationRetry() throws Exception {
        createApplicationRetry = true;

        createApp();
    }

    @Test
    public void createAppCreateManifestFileError() throws Exception {
        createManifestFileError = true;

        createApp();
    }

    @Test
    public void createAppCreateRouteError() throws Exception {
        createRouteError = true;

        createApp();
    }

    @Test
    public void createAppRouteMappingError() throws Exception {
        routeMappingError = true;

        createApp();
    }

    @Test
    public void createTempFile() throws Exception {
        Software software = new Software();
        software.setApp("helloworld.ext");
        software.setAppPath("my/path/to");

        given(container.getObject(any())).willReturn(storedObject);
        given(storedObject.downloadObject()).willReturn(new byte[] { 'x' });

        given(appService.createTempFile(any())).willCallRealMethod();

        File result = appService.createTempFile(software);
        assertEquals(true, result.exists());
    }

    @Test(expected = PlatformException.class)
    public void createTempFileError() throws Exception {
        Software software = new Software();

        given(appService.createTempFile(any())).willCallRealMethod();

        appService.createTempFile(software);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createManifestFile() throws Exception {
        Software software = new Software();
        software.setManifest("manifest.yml");
        software.setManifestPath("my/path/to");

        given(container.getObject(any())).willReturn(storedObject);
        given(storedObject.downloadObject()).willReturn("app: \n  name: foo".getBytes());

        given(appService.createManifestFile(any())).willCallRealMethod();
        given(appService.convertYamlToJson(any())).willCallRealMethod();

        Map<?, ?> result = appService.createManifestFile(software);
        assertEquals("foo", ((Map<String, Map<String, String>>) result.get("app")).get("name"));
    }

    @Test
    public void createManifestFileError() throws Exception {
        Software software = new Software();

        given(appService.createManifestFile(any())).willCallRealMethod();

        Map<?, ?> result = appService.createManifestFile(software);
        assertEquals(true, result.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void convertYamlToJsonError() {
        File file = new File(UUID.randomUUID().toString());
        given(appService.convertYamlToJson(any())).willCallRealMethod();

        appService.convertYamlToJson(file);
    }

    @Test
    public void getBrowser() {
        given(appService.getBrowser(any())).willCallRealMethod();

        String result;

        result = appService.getBrowser("MSIE-1.0");
        assertEquals("MSIE", result);

        result = appService.getBrowser("Chrome-1.0");
        assertEquals("Chrome", result);

        result = appService.getBrowser("Opera-1.0");
        assertEquals("Opera", result);

        result = appService.getBrowser("Trident/7.0");
        assertEquals("MSIE", result);

        result = appService.getBrowser("UNKNOWN-1.0");
        assertEquals("Firefox", result);
    }

    @Test
    public void getDisposition() throws Exception {
        given(appService.getDisposition(any(), any())).willCallRealMethod();

        String result;

        result = appService.getDisposition("My File", "MSIE");
        assertEquals("My%20File", result);

        result = appService.getDisposition("My File", "Firefox");
        assertEquals("\"My File\"", result);

        result = appService.getDisposition("My File", "Opera");
        assertEquals("\"My File\"", result);

        result = appService.getDisposition("My 파일", "Chrome");
        assertEquals("My %ED%8C%8C%EC%9D%BC", result);
    }

    @Test(expected = RuntimeException.class)
    public void getDispositionError() throws Exception {
        given(appService.getDisposition(any(), any())).willCallRealMethod();

        appService.getDisposition("My 파일", "UNKNOWN");
    }

    @Test
    public void createApplication() throws Exception {
        App app = new App();
        app.setBuildpack("x");
        app.setMemory(1);
        app.setAppName("x");
        app.setDiskQuota(1);
        app.setSpaceGuid("x");

        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.create(any())).willReturn(createApplicationResponseMono);
        Metadata metadata = Metadata.builder().id("1234").build();
        CreateApplicationResponse createApplicationResponse = CreateApplicationResponse.builder().metadata(metadata)
                .build();
        given(createApplicationResponseMono.block()).willReturn(createApplicationResponse);

        given(appService.createApplication(any(), any())).willCallRealMethod();

        String result = appService.createApplication(app, cloudFoundryClient);
        assertEquals("1234", result);
    }

    @Test
    public void createRoute() {
        App app = new App();
        app.setHostName("x");
        app.setDomainId("x");
        app.setSpaceGuid("x");

        given(cloudFoundryClient.routes()).willReturn(routes);
        given(routes.create(any())).willReturn(createRouteResponseMono);
        Metadata metadata = Metadata.builder().id("1234").build();
        CreateRouteResponse createRouteResponse = CreateRouteResponse.builder().metadata(metadata).build();
        given(createRouteResponseMono.block()).willReturn(createRouteResponse);

        given(appService.createRoute(any(), any())).willCallRealMethod();

        String result = appService.createRoute(app, cloudFoundryClient);
        assertEquals("1234", result);
    }

    @Test
    public void routeMapping() throws Exception {
        given(cloudFoundryClient.routeMappings()).willReturn(routeMappings);
        given(routeMappings.create(any())).willReturn(createRouteMappingResponse);

        doCallRealMethod().when(appService).routeMapping(any(), any(), any());

        appService.routeMapping("x", "x", cloudFoundryClient);
    }

    @Test
    public void fileUpload() throws Exception {
        File file = new File("my/file");

        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.upload(any())).willReturn(uploadApplicationResponseMono);

        UploadApplicationResponse uploadApplicationResponse = UploadApplicationResponse.builder().build();
        given(uploadApplicationResponseMono.block()).willReturn(uploadApplicationResponse);

        doCallRealMethod().when(appService).fileUpload(any(), any(), any());

        appService.fileUpload(file, "x", cloudFoundryClient);
    }

    @Test(expected = PlatformException.class)
    public void fileUploadError() throws Exception {
        File file = new File("my/file");

        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.upload(any())).willReturn(uploadApplicationResponseMono);

        given(uploadApplicationResponseMono.block()).willThrow(new RuntimeException());

        doCallRealMethod().when(appService).fileUpload(any(), any(), any());

        appService.fileUpload(file, "x", cloudFoundryClient);
    }

    @Test
    public void updateApp() {
        Map<String, String> envJson = null;
        if (!envJsonNull) {
            envJson = new TreeMap<>();
            if (!envJsonEmpty) {
                envJson.put("x", "x");
            }
            if (envJsonSizeMinus) {
                envJson = stringStringMap;
                // FIXME:
                given(envJson.size()).willReturn(-1);
            }
        }

        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.update(any())).willReturn(updateApplicationResponseMono);

        given(appService.updateApp(any(), any())).willCallRealMethod();

        Map<?, ?> result = appService.updateApp(envJson, "x");
        assertEquals(true, result.get("result"));
    }

    @Test
    public void updateAppEnvJsonNull() {
        envJsonNull = true;

        updateApp();
    }

    @Test
    public void updateAppEnvJsonEmpty() {
        envJsonEmpty = true;

        updateApp();
    }

    @Test
    public void updateAppEnvJsonSizeMinus() {
        envJsonSizeMinus = true;

        updateApp();
    }

    @Test
    public void updateAppEmpty() {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.update(any())).willReturn(updateApplicationResponseMono);

        given(appService.updateApp(any(), any())).willCallRealMethod();

        Map<?, ?> result = appService.updateApp(new TreeMap<>(), "x");
        assertEquals(true, result.get("result"));
    }

    @Test
    public void updateAppError() {
        given(appService.cloudFoundryClient(any())).willThrow(new RuntimeException());

        given(appService.updateApp(any(), any())).willCallRealMethod();

        Map<?, ?> result = appService.updateApp(new TreeMap<>(), "x");
        assertEquals(false, result.get("result"));
    }

    @Test
    public void procStartApplication() {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.update(any())).willReturn(updateApplicationResponseMono);

        given(appService.procStartApplication(any())).willCallRealMethod();

        Map<String, Object> result = appService.procStartApplication("x");
        assertEquals("success", result.get("RESULT"));
    }

    @Test
    public void procStartApplicationError() {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willThrow(new RuntimeException());

        given(appService.procStartApplication(any())).willCallRealMethod();

        Map<String, Object> result = appService.procStartApplication("x");
        assertEquals("success", result.get("RESULT"));
    }

    @Test
    public void timer() {
        doCallRealMethod().when(appService).timer(any(Integer.class));

        appService.timer(1);
    }

    @Test
    public void timerInterrupt() {
        doCallRealMethod().when(appService).timer(any(Integer.class));

        Thread t = new Thread(() -> {
            appService.timer(1);
        });
        t.start();
        t.interrupt();
    }

    @Test
    public void getApplicationNameExists() throws PlatformException {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.list(any())).willReturn(listApplicationsResponseMono);
        ApplicationEntity applicationEntity = ApplicationEntity.builder().name(applicationName).build();
        ApplicationResource applicationResource = ApplicationResource.builder().entity(applicationEntity).build();
        ListApplicationsResponse listApplicationsResponse = ListApplicationsResponse.builder()
                .resource(applicationResource).build();
        given(listApplicationsResponseMono.block()).willReturn(listApplicationsResponse);

        given(appService.getApplicationNameExists(any())).willCallRealMethod();

        ApplicationEntity result = appService.getApplicationNameExists("x");
        if ("x".equals(applicationName)) {
            assertEquals(applicationEntity, result);
        } else {
            assertNull(result);
        }
    }

    @Test
    public void getApplicationNameExistsFalse() throws PlatformException {
        applicationName = "y";

        getApplicationNameExists();
    }

    @Test(expected = PlatformException.class)
    public void getApplicationNameExistsError() throws PlatformException {
        given(appService.cloudFoundryClient(any())).willThrow(new RuntimeException());

        given(appService.getApplicationNameExists(any())).willCallRealMethod();

        appService.getApplicationNameExists("x");
    }

    @Test
    public void getApp() {
        Instance instance = new Instance();
        instance.setAppGuid("x");

        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.get(any())).willReturn(getApplicationResponseMono);
        GetApplicationResponse getApplicationResponse = GetApplicationResponse.builder().build();
        given(getApplicationResponseMono.block()).willReturn(getApplicationResponse);

        given(appService.getApp(any())).willCallRealMethod();

        GetApplicationResponse result = appService.getApp(instance);
        assertEquals(getApplicationResponse, result);
    }

    @Test
    public void getRouteMappingList() {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.routeMappings()).willReturn(routeMappings);
        given(routeMappings.list(any())).willReturn(listRouteMappingsResponseMono);
        ListRouteMappingsResponse listRouteMappingsResponse = ListRouteMappingsResponse.builder().build();
        given(listRouteMappingsResponseMono.block()).willReturn(listRouteMappingsResponse);

        given(appService.getRouteMappingList(any())).willCallRealMethod();

        ListRouteMappingsResponse result = appService.getRouteMappingList("x");
        assertEquals(listRouteMappingsResponse, result);
    }

    @Test
    public void removeApplicationRoute() {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.removeRoute(any())).willReturn(removeRouteMono);
        given(cloudFoundryClient.routes()).willReturn(routes);
        given(routes.delete(any())).willReturn(deleteRouteResponseMono);

        given(appService.removeApplicationRoute(any(), any())).willCallRealMethod();

        Map<?, ?> result = appService.removeApplicationRoute("x", "x");
        assertEquals(true, result.get("result"));
    }

    @Test
    public void removeApplicationRouteError() {
        given(appService.cloudFoundryClient(any())).willThrow(new RuntimeException());

        given(appService.removeApplicationRoute(any(), any())).willCallRealMethod();

        Map<?, ?> result = appService.removeApplicationRoute("x", "x");
        assertEquals(false, result.get("result"));
    }

    @Test
    public void deleteApp() {
        given(appService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.applicationsV2()).willReturn(applicationsV2);
        given(applicationsV2.delete(any())).willReturn(deleteApplicationMono);

        given(appService.deleteApp(any())).willCallRealMethod();

        Map<?, ?> result = appService.deleteApp("x");
        assertEquals(true, result.get("result"));
    }

    @Test
    public void deleteAppError() {
        given(appService.cloudFoundryClient(any())).willThrow(new RuntimeException());

        given(appService.deleteApp(any())).willCallRealMethod();

        Map<?, ?> result = appService.deleteApp("x");
        assertEquals(false, result.get("result"));
    }

    @Test
    public void getRecentLog() {
        given(appService.dopplerClient(any(), any())).willReturn(reactorDopplerClient);
        given(reactorDopplerClient.recentLogs(any())).willReturn(envelopeFlux);
        given(envelopeFlux.collectList()).willReturn(listEnvelopeMono);
        List<Envelope> listEnvelope = new ArrayList<>();
        listEnvelope.add(Envelope.builder().eventType(EventType.ERROR).origin("x").build());
        given(listEnvelopeMono.block()).willReturn(listEnvelope);

        given(appService.getRecentLog(any())).willCallRealMethod();

        List<Envelope> result = appService.getRecentLog("x");
        assertEquals(listEnvelope, result);
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
