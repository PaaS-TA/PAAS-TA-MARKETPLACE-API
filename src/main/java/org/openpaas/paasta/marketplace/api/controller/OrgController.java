package org.openpaas.paasta.marketplace.api.controller;

import org.cloudfoundry.client.v2.organizations.OrganizationResource;
import org.openpaas.paasta.marketplace.api.model.Org;
import org.openpaas.paasta.marketplace.api.model.Quota;
import org.openpaas.paasta.marketplace.api.model.QuotaList;
import org.openpaas.paasta.marketplace.api.service.OrgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Organization Controller
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-01-29
 */
@RestController
@RequestMapping(value = "/orgs")
public class OrgController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrgController.class);
    private static final String CF_AUTHORIZATION_HEADER_KEY = "cf-Authorization";

    @Autowired
    private OrgService orgService;

    @GetMapping
    public List<OrganizationResource> getOrgsForUser(){

        LOGGER.debug("Org list for admin");
//        LOGGER.info("토큰은??? " + token);

        return orgService.getOrgsForUser();
    }

    /**
     * 조직명 중복검사를 실행한다.
     *
     * @param orgName     the org
     * @return boolean
     */
    @GetMapping(value = "/{orgName}/exist")
    public boolean isExistOrgName(@PathVariable String orgName) {
        LOGGER.info("orgName 은 ??? " + orgName);
        return orgService.isExistOrgName(orgName);
    }


    @PostMapping(value = "/quotas")
    public Quota createOrgQuota(@RequestBody Quota quota){
        return orgService.createOrgQuota(quota);
    }


    @PostMapping
    public Org createOrg(@RequestBody Org org,
                         @RequestHeader(CF_AUTHORIZATION_HEADER_KEY) String token){
        return orgService.createOrg(org, token);
    }

    @GetMapping(value = "/quotas")
    public QuotaList getOrgQuotas(){
        return orgService.getOrgQuotas();
    }
}
