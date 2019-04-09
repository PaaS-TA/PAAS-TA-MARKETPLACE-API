package org.openpaas.paasta.marketplace.api.service;

import org.cloudfoundry.client.v2.spaces.*;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.openpaas.paasta.marketplace.api.common.Common;
import org.openpaas.paasta.marketplace.api.common.CommonService;
import org.openpaas.paasta.marketplace.api.model.Space;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Space 서비스
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-03-14
 */
@Service
public class SpaceService extends Common {

    @Autowired
    OrgService orgService;

    @Autowired
    CommonService commonService;

    private static final Logger LOGGER = LoggerFactory.getLogger(Common.class);

    public ListSpacesResponse getSpacesWithOrgName(String orgId, String adminToken) {
        ReactorCloudFoundryClient cloudFoundryClient = Common.cloudFoundryClient(connectionContext(), tokenProvider(adminToken));
        return getSpaces(orgId, cloudFoundryClient);
    }

    /**
     * 공간(스페이스) 목록 조회한다.
     *
     * @param orgId
     * @param reactorCloudFoundryClient
     * @return
     */
    public ListSpacesResponse getSpaces(String orgId, ReactorCloudFoundryClient reactorCloudFoundryClient) {
        ListSpacesResponse response = reactorCloudFoundryClient.spaces().list(ListSpacesRequest.builder().organizationId(orgId).build()).block();

        return response;
    }

    /**
     * 공간(스페이스)을 생성한다.
     *
     * @param space
     * @return
     */
    public Space createSpace(Space space, String token) {
        Map resultMap = new HashMap();

        try {

            CreateSpaceResponse response = Common.cloudFoundryClient(connectionContext(), tokenProvider(token)).spaces().create(CreateSpaceRequest.builder().name(space.getName()).organizationId(space.getOrgGuid()).build()).block();

            LOGGER.info("space guid::: " + response.getMetadata().getId() + " & userGuidcf  :: " + space.getUserId());

            associateSpaceManager(response.getMetadata().getId(), space.getUserId());
            associateSpaceDeveloper(response.getMetadata().getId(), space.getUserId());
            associateSpaceAuditor(response.getMetadata().getId(), space.getUserId());

            // Results for association roles will be disposed
            //associateSpaceUserRolesByOrgIdAndRole(response.getMetadata().getId(), space.getOrgGuid() );

            resultMap = objectMapper.convertValue(response, Map.class);

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", false);
            resultMap.put("msg", e);
        }

        return commonService.setResultObject(resultMap, Space.class);
    }


    /**
     * 해당 Space 에 Manager 권한 부여
     *
     * @param spaceId
     * @param userId
     * @return
     */
    private AssociateSpaceManagerResponse associateSpaceManager(String spaceId, String userId) {
        return Common.cloudFoundryClient(connectionContext(), tokenProvider()).spaces().associateManager(AssociateSpaceManagerRequest.builder().spaceId(spaceId).managerId(userId).build()).block();
    }


    /**
     * 해당 Space 에 Developer 권한 부여
     *
     * @param spaceId
     * @param userId
     * @return
     */
    private AssociateSpaceDeveloperResponse associateSpaceDeveloper(String spaceId, String userId) {
        return Common.cloudFoundryClient(connectionContext(), tokenProvider()).spaces().associateDeveloper(AssociateSpaceDeveloperRequest.builder().spaceId(spaceId).developerId(userId).build()).block();
    }


    /**
     * 해당 Space 에 Auditor 권한 부여
     *
     * @param spaceId
     * @param userId
     * @return
     */
    private AssociateSpaceAuditorResponse associateSpaceAuditor(String spaceId, String userId) {
        return Common.cloudFoundryClient(connectionContext(), tokenProvider()).spaces().associateAuditor(AssociateSpaceAuditorRequest.builder().spaceId(spaceId).auditorId(userId).build()).block();
    }


}
