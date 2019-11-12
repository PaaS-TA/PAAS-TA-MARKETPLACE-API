package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.cloudfoundry.client.v2.spaces.AssociateSpaceAuditorResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceDeveloperResponse;
import org.cloudfoundry.client.v2.spaces.AssociateSpaceManagerResponse;
import org.cloudfoundry.client.v2.spaces.Spaces;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;
import org.cloudfoundry.client.v3.spaces.SpacesV3;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class SpaceServiceTest {

    @Mock
    SpaceService spaceService;

    @Mock
    ReactorCloudFoundryClient cloudFoundryClient;
    
    @Mock
    SpacesV3 spacesV3;
    
    @Mock
    Spaces spaces;

    @Mock
    Mono<CreateSpaceResponse> createSpaceResponseMono;
    
    @Mock
    Mono<AssociateSpaceManagerResponse> associateSpaceManagerResponseMono;
    
    @Mock
    Mono<AssociateSpaceDeveloperResponse> associateSpaceDeveloperResponseMono;
    
    @Mock
    Mono<AssociateSpaceAuditorResponse> associateSpaceAuditorResponseMono;
    
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void constructor() {
        spaceService = new SpaceService();
    }

    @Test
    public void createSpace() {
        given(spaceService.cloudFoundryClient(any())).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.spacesV3()).willReturn(spacesV3);
        given(spacesV3.create(any())).willReturn(createSpaceResponseMono);
        CreateSpaceResponse createSpaceResponse = CreateSpaceResponse.builder().id("1234").name("x").createdAt("x").build();
        given(createSpaceResponseMono.block()).willReturn(createSpaceResponse);
        
        given(spaceService.cloudFoundryClient()).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.spaces()).willReturn(spaces);
        given(spaces.associateManager(any())).willReturn(associateSpaceManagerResponseMono);
        given(spaces.associateDeveloper(any())).willReturn(associateSpaceDeveloperResponseMono);
        given(spaces.associateAuditor(any())).willReturn(associateSpaceAuditorResponseMono);
        
        given(spaceService.createSpace(any(), any(), any(), any())).willCallRealMethod();
        
        CreateSpaceResponse result = spaceService.createSpace("x", "x", "x", "x");
        assertEquals(createSpaceResponse, result);
    }

}
