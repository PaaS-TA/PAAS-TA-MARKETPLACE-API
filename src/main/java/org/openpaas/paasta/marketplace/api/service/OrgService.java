package org.openpaas.paasta.marketplace.api.service;

import org.cloudfoundry.client.v2.organizationquotadefinitions.*;
import org.cloudfoundry.client.v2.organizations.*;
import org.cloudfoundry.operations.organizations.OrganizationDetail;
import org.cloudfoundry.operations.organizations.OrganizationInfoRequest;
import org.cloudfoundry.reactor.TokenProvider;
import org.openpaas.paasta.marketplace.api.common.Common;
import org.openpaas.paasta.marketplace.api.common.CommonService;
import org.openpaas.paasta.marketplace.api.model.Org;
import org.openpaas.paasta.marketplace.api.model.Quota;
import org.openpaas.paasta.marketplace.api.model.QuotaList;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

import org.cloudfoundry.client.v3.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v3.organizations.CreateOrganizationResponse;

/**
 * Org 서비스
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-03-14
 */
@Service
public class OrgService extends Common {

    private final Logger LOGGER = getLogger(this.getClass());

    @Autowired
    private CommonService commonService;

    public String getOrgId(String orgName, String adminToken) {
        return getOrgUsingName(orgName, adminToken).getId();
    }

    public List<OrganizationResource> getOrgsForUser() {
        return Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).organizations().list(ListOrganizationsRequest.builder().build()).block().getResources();
    }

//    public Org createOrg(Org org, String adminToken){
//        // Org 생성
//        CreateOrganizationResponse organization = Common.cloudFoundryClient(connectionContext(), tokenProvider(adminToken)).organizations().create(CreateOrganizationRequest.builder().name(org.getName()).quotaDefinitionId(org.getQuotaGuid()).build()).block();
//
//        Map resultMap = objectMapper.convertValue(organization, Map.class);
//        LOGGER.info("이거 되는거니????????? " + resultMap.toString());
//
//        return commonService.setResultObject(resultMap, Org.class);
//    }

    /**
     * V3 버전 - ORG 생성
     *
     * @param org
     * @param token
     * @return
     */
    public Org createOrg(Org org, String token){
        LOGGER.info("uaa 통과한 admin token ::: " + token);

        // Org 생성
        CreateOrganizationResponse organization = Common.cloudFoundryClient(connectionContext(), tokenProvider(token)).organizationsV3()
                .create(CreateOrganizationRequest.builder()
                        .name(org.getName())
                        .build()).block();

        Map resultMap = objectMapper.convertValue(organization, Map.class);
        LOGGER.info("이거 되는거니????????? " + resultMap.toString());

        return commonService.setResultObject(resultMap, Org.class);
    }


    public boolean isExistOrgName(final String orgName) {
        try {
            return orgName.equals(getOrgUsingName(orgName, getToken()).getName());
        } catch (Exception e) {
            return false;
        }
    }


    private OrganizationDetail getOrgUsingName(final String name, final String token) {
        final TokenProvider internalTokenProvider;
        if (null != token && !"".equals(token)) {
            internalTokenProvider = tokenProvider(token);
        }
        else{
            internalTokenProvider = tokenProvider();
        }

        return Common.cloudFoundryOperations(connectionContext(), internalTokenProvider).organizations().get(OrganizationInfoRequest.builder().name(name).build()).block();
    }


    /**
     * Org Manager 권한 부여
     *
     * @param orgId
     * @param userId
     * @param token
     * @return AssociateOrganizationManagerResponse
     */
    private AssociateOrganizationManagerResponse associateOrgManager(String orgId, String userId, String token) {
        return Common.cloudFoundryClient(connectionContext(), tokenProvider(token)).organizations().associateManager(AssociateOrganizationManagerRequest.builder().organizationId(orgId).managerId(userId).build()).block();
    }


    /**
     * Billing Manager 권한 부여
     *
     * @param orgId
     * @param userId
     * @return
     */
    // Todo :: 수정 필요
    private AssociateOrganizationBillingManagerResponse associateBillingManager(String orgId, String userId) {
        return Common.cloudFoundryClient(connectionContext(), tokenProvider()).organizations().associateBillingManager(AssociateOrganizationBillingManagerRequest.builder().organizationId(orgId).billingManagerId(userId).build()).block();
    }


    /**
     * Org Auditor 권한 부여
     *
     * @param orgId
     * @param userId
     * @return
     */
    // Todo :: 수정 필요
    private AssociateOrganizationAuditorResponse associateOrgAuditor(String orgId, String userId) {
        return Common.cloudFoundryClient(connectionContext(), tokenProvider()).organizations().associateAuditor(AssociateOrganizationAuditorRequest.builder().organizationId(orgId).auditorId(userId).build()).block();
    }


    /**
     * Org Quota 생성
     *
     * @param quota
     * @return
     */
    public Quota createOrgQuota(Quota quota){
        CreateOrganizationQuotaDefinitionResponse orgQuotaDefinition = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).organizationQuotaDefinitions().create(CreateOrganizationQuotaDefinitionRequest.builder()
                .name(quota.getName())
                .nonBasicServicesAllowed(quota.isNonBasicServicesAllowed())
                .totalServices(quota.getTotalServices())
                .totalRoutes(quota.getTotalRoutes())
                .totalReservedRoutePorts(quota.getTotalReservedRoutePorts())
                .memoryLimit(quota.getMemoryLimit())
                .instanceMemoryLimit(quota.getInstanceMemoryLimit())
                .build()).block();

        Map resultMap = objectMapper.convertValue(orgQuotaDefinition, Map.class);

        return commonService.setResultObject(resultMap, Quota.class);
    }



    public Quota getOrgQuota(String orgQuotaGuid){
        GetOrganizationQuotaDefinitionResponse orgQuota = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).organizationQuotaDefinitions().get(GetOrganizationQuotaDefinitionRequest.builder().organizationQuotaDefinitionId(orgQuotaGuid).build()).block();

        Map resultMap = objectMapper.convertValue(orgQuota, Map.class);

        return commonService.setResultObject(resultMap, Quota.class);
    }

    public QuotaList getOrgQuotas(){
        ListOrganizationQuotaDefinitionsResponse orgQuotas = Common.cloudFoundryClient(connectionContext(), tokenProvider(getToken())).organizationQuotaDefinitions().list(ListOrganizationQuotaDefinitionsRequest.builder().build()).block();

        Map resultMap = objectMapper.convertValue(orgQuotas, Map.class);

        return commonService.setResultObject(resultMap, QuotaList.class);
    }

}
