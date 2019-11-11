package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cloudfoundry.client.v2.applications.ApplicationsV2;
import org.cloudfoundry.client.v2.applications.ListApplicationsResponse;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.javaswift.joss.model.Container;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.springframework.test.util.ReflectionTestUtils;

import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class AppServiceTest {

    AppService appService;

    @Mock
    Container container;

    @Mock
    ApplicationsV2 applicationsV2;

    @Mock
    Mono<ListApplicationsResponse> listApplicationsResponseMono;

    @Mock
    ReactorCloudFoundryClient reactorCloudFoundryClient;

    @Mock
    ReactorCloudFoundryClient cloudFoundryClient;

    @Before
    public void setUp() throws Exception {
        appService = mock(AppService.class);

        ReflectionTestUtils.setField(appService, "marketSpaceGuid", "x");
        ReflectionTestUtils.setField(appService, "marketDomainGuid", "x");
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
        given(appService.cloudFoundryClient(any())).willReturn(reactorCloudFoundryClient);

        Map<String, Object> manifestFile = new TreeMap<>();
        List<Object> applications = new ArrayList<>();
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("instances", 1);
        List<String> buildpacks = new ArrayList<>();
        buildpacks.add("x");
        resultMap.put("buildpacks", buildpacks);
        applications.add(resultMap);
        manifestFile.put("applications", applications);
        given(appService.createManifestFile(any())).willReturn(manifestFile);

        given(appService.createApp(any(), any(), any(), any())).willCallRealMethod();

        Software software = new Software();
        appService.createApp(software, "x", "4G", "30G");
    }

}
