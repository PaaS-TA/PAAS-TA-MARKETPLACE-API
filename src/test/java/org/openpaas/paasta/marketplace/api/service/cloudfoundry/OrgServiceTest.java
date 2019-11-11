package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizationquotadefinitions.OrganizationQuotaDefinitions;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.client.v2.organizations.Organizations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.organizations.OrganizationDetail;
import org.cloudfoundry.operations.organizations.OrganizationQuota;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.Org;

import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class OrgServiceTest {

    OrgService orgService;

    @Mock
    OrganizationQuotaDefinitions organizationQuotaDefinitions;

    @Mock
    Organizations organizations;

    @Mock
    org.cloudfoundry.operations.organizations.Organizations _organizations;

    @Mock
    DefaultConnectionContext connectionContext;

    @Mock
    PasswordGrantTokenProvider tokenProvider;

    @Mock
    Mono<ListOrganizationQuotaDefinitionsResponse> organizationQuotaDefinitionsMono;

    @Mock
    Mono<CreateOrganizationResponse> createOrganizationResponseMono;

    @Mock
    Mono<OrganizationDetail> organizationDetailMono;

    @Mock
    ReactorCloudFoundryClient cloudFoundryClient;

    @Mock
    DefaultCloudFoundryOperations defaultCloudFoundryOperations;

    @Before
    public void setUp() throws Exception {
        orgService = mock(OrgService.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void constructor() {
        orgService = new OrgService();
    }

    @Test
    public void isExistOrgByOrgName() {
        OrganizationQuota quota = mock(OrganizationQuota.class);
        OrganizationDetail organizationDetail = OrganizationDetail.builder().id("x").quota(quota).name("x").build();
        given(orgService.getOrgUsingName(any(String.class))).willReturn(organizationDetail);

        given(orgService.isExistOrgByOrgName(any(String.class))).willCallRealMethod();

        boolean result = orgService.isExistOrgByOrgName("x");
        assertEquals(true, result);
    }

    @Test
    public void isExistOrgByOrgNameWithError() {
        given(orgService.getOrgUsingName(any(String.class))).willThrow(new RuntimeException());

        given(orgService.isExistOrgByOrgName(any(String.class))).willCallRealMethod();

        boolean result = orgService.isExistOrgByOrgName("x");
        assertEquals(false, result);
    }

    @Test
    public void getOrgUsingName() {
        OrganizationQuota quota = mock(OrganizationQuota.class);
        OrganizationDetail organizationDetail = OrganizationDetail.builder().id("x").quota(quota).name("x").build();
        given(orgService.getOrgUsingName(any(String.class), any())).willReturn(organizationDetail);

        given(orgService.getOrgUsingName(any(String.class))).willCallRealMethod();

        OrganizationDetail result = orgService.getOrgUsingName("x");
        assertEquals(organizationDetail, result);
    }

    @Test
    public void getOrgUsingNameWithToken() {
        given(orgService.tokenProvider(any())).willReturn(tokenProvider);
        given(orgService.connectionContext()).willReturn(null);
        given(orgService.cloudFoundryOperations(any(), any())).willReturn(defaultCloudFoundryOperations);
        given(defaultCloudFoundryOperations.organizations()).willReturn(_organizations);
        given(_organizations.get(any())).willReturn(organizationDetailMono);
        OrganizationQuota quota = mock(OrganizationQuota.class);
        OrganizationDetail organizationDetail = OrganizationDetail.builder().id("x").quota(quota).name("x").build();
        given(organizationDetailMono.block()).willReturn(organizationDetail);

        given(orgService.getOrgUsingName(any(String.class), any())).willCallRealMethod();

        OrganizationDetail result;
        result = orgService.getOrgUsingName("x", "token");
        assertEquals(organizationDetail, result);

        result = orgService.getOrgUsingName("x", null);
        assertEquals(organizationDetail, result);
        
        result = orgService.getOrgUsingName("x", "");
        assertEquals(organizationDetail, result);
    }

    @Test
    public void getOrgQuotaDefinitionsList() {
        given(orgService.cloudFoundryClient()).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.organizationQuotaDefinitions()).willReturn(organizationQuotaDefinitions);
        given(organizationQuotaDefinitions.list(any())).willReturn(organizationQuotaDefinitionsMono);
        ListOrganizationQuotaDefinitionsResponse listOrganizationQuotaDefinitionsResponse = ListOrganizationQuotaDefinitionsResponse
                .builder().build();
        given(organizationQuotaDefinitionsMono.block()).willReturn(listOrganizationQuotaDefinitionsResponse);

        given(orgService.getOrgQuotaDefinitionsList()).willCallRealMethod();

        ListOrganizationQuotaDefinitionsResponse resutl = orgService.getOrgQuotaDefinitionsList();
        assertEquals(listOrganizationQuotaDefinitionsResponse, resutl);
    }

    @Test
    public void createOrg() {
        given(orgService.tokenProvider()).willReturn(tokenProvider);
        given(orgService.cloudFoundryClient(any(PasswordGrantTokenProvider.class))).willReturn(cloudFoundryClient);
        given(cloudFoundryClient.organizations()).willReturn(organizations);
        given(organizations.create(any())).willReturn(createOrganizationResponseMono);
        CreateOrganizationResponse createOrganizationResponse = CreateOrganizationResponse.builder().build();
        given(createOrganizationResponseMono.block()).willReturn(createOrganizationResponse);

        given(orgService.createOrg(any(Org.class))).willCallRealMethod();
        Org org = new Org();
        org.setOrgName("x");
        org.setQuotaGuid("x");

        CreateOrganizationResponse resutl = orgService.createOrg(org);
        assertEquals(createOrganizationResponse, resutl);
    }

}
