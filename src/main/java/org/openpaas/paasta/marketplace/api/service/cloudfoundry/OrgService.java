package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsRequest;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationRequest;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.cloudfoundry.operations.organizations.OrganizationDetail;
import org.cloudfoundry.operations.organizations.OrganizationInfoRequest;
import org.cloudfoundry.reactor.TokenProvider;
import org.openpaas.paasta.marketplace.api.config.common.Common;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.Org;
import org.springframework.stereotype.Service;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-19
 */
@Service
public class OrgService extends Common {

    /**
     * Org 이름으로 Org 존재 유무 확인
     *
     * @param orgName
     * @return
     */
    public boolean isExistOrgByOrgName(String orgName) {
        try {
            return orgName.equals(getOrgUsingName(orgName).getName());
        } catch (Exception e) {
            return false;
        }
    }

    public OrganizationDetail getOrgUsingName(String name) {
        return getOrgUsingName(name, null);
    }

    public OrganizationDetail getOrgUsingName(String name, String token) {
        final TokenProvider internalTokenProvider;
        if (null != token && !"".equals(token)) internalTokenProvider = tokenProvider(token);
        else internalTokenProvider = tokenProvider();

        return cloudFoundryOperations(connectionContext(), internalTokenProvider).organizations().get(OrganizationInfoRequest.builder().name(name).build()).block();
    }

    /**
     * Org 쿼터 목록 조회
     *
     * @return
     */
    public ListOrganizationQuotaDefinitionsResponse getOrgQuotaDefinitionsList() {
        return cloudFoundryClient().organizationQuotaDefinitions().list(ListOrganizationQuotaDefinitionsRequest.builder().build()).block();
    }

    public CreateOrganizationResponse createOrg(Org org) {
        return cloudFoundryClient(tokenProvider()).organizations().create(CreateOrganizationRequest.builder().name(org.getOrgName()).quotaDefinitionId(org.getQuotaGuid()).build()).block();
    }
}
