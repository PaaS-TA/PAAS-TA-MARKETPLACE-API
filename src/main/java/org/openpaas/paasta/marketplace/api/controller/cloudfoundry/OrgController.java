package org.openpaas.paasta.marketplace.api.controller.cloudfoundry;

import lombok.RequiredArgsConstructor;
import org.cloudfoundry.client.v2.organizationquotadefinitions.ListOrganizationQuotaDefinitionsResponse;
import org.cloudfoundry.client.v2.organizations.CreateOrganizationResponse;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.Org;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.OrgService;
import org.springframework.web.bind.annotation.*;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-19
 */
@RestController
@RequiredArgsConstructor
public class OrgController {
    private final OrgService orgService;

    /**
     * Org 이름으로 Org 존재 유무 확인
     *
     * @param orgName
     * @return
     */
    @GetMapping(value = "/v3/orgs/{orgName}/exist")
    public boolean isExistOrgByOrgName(@PathVariable String orgName) {
        return orgService.isExistOrgByOrgName(orgName);
    }


    /**
     * Org 쿼터 목록 조회
     *
     * @return
     */
    @GetMapping(value = "/v3/orgs/quota-definitions")
    public ListOrganizationQuotaDefinitionsResponse getOrgQuotaDefinitions() {
        // TODO ::: 목록[0] 의 guid => orgService.getOrgQuotaDefinitionsList().getResources().get(0).getMetadata().getId()
        return orgService.getOrgQuotaDefinitionsList();
    }

    /**
     * Org 조직 생성
     *
     * "orgName": "market-org",
     * "quotaGuid": "d50c8dcd-7066-4e1b-a17a-6d8c4c172e9d"
     *
     * @param org
     * @return
     */
    @PostMapping(value = "/v3/orgs")
    public CreateOrganizationResponse createOrg(@RequestBody Org org) {
        return orgService.createOrg(org);
    }
}
