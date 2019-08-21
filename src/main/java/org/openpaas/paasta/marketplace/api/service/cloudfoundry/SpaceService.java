package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import org.cloudfoundry.client.v2.spaces.*;
import org.cloudfoundry.client.v3.Relationship;
import org.cloudfoundry.client.v3.ToOneRelationship;
import org.cloudfoundry.client.v3.spaces.SpaceRelationships;
import org.openpaas.paasta.marketplace.api.config.common.Common;
import org.springframework.stereotype.Service;
import org.cloudfoundry.client.v3.spaces.CreateSpaceResponse;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-19
 */
@Service
public class SpaceService extends Common {

    /**
     * 공간 Space 생성
     *
     * @param name
     * @param organizationId
     * @param usrid
     * @return
     */
    public CreateSpaceResponse createSpace(String name, String organizationId, String usrid, String token) {

        CreateSpaceResponse response =  cloudFoundryClient(tokenProvider(token)).spacesV3().create(org.cloudfoundry.client.v3.spaces.CreateSpaceRequest.builder().name(name).relationships(SpaceRelationships.builder().organization(ToOneRelationship.builder().data(Relationship.builder().id(organizationId).build()).build()).build()).build()).block();

        associateSpaceManager(response.getId(), usrid);
        associateSpaceDeveloper(response.getId(), usrid);
        associateSpaceAuditor(response.getId(), usrid);
        return response;
    }

    private AssociateSpaceManagerResponse associateSpaceManager(String spaceId, String userId) {
        return cloudFoundryClient().spaces().associateManager(AssociateSpaceManagerRequest.builder().spaceId(spaceId).managerId(userId).build()).block();
    }

    private AssociateSpaceDeveloperResponse associateSpaceDeveloper(String spaceId, String userId) {
        return cloudFoundryClient().spaces().associateDeveloper(AssociateSpaceDeveloperRequest.builder().spaceId(spaceId).developerId(userId).build()).block();
    }

    private AssociateSpaceAuditorResponse associateSpaceAuditor(String spaceId, String userId) {
        return cloudFoundryClient().spaces().associateAuditor(AssociateSpaceAuditorRequest.builder().spaceId(spaceId).auditorId(userId).build()).block();
    }
}
