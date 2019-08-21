package org.openpaas.paasta.marketplace.api.controller.cloudfoundry;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.config.common.Common;
import org.openpaas.paasta.marketplace.api.cloudFoundryModel.Space;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.SpaceService;
import org.openpaas.paasta.marketplace.api.service.cloudfoundry.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-19
 */
@RestController
@RequiredArgsConstructor
public class SpaceController {

//    @Value("{cloudfoundry.user.admin.username}")
//    private final String cfAdminName;

    private final SpaceService spaceService;

    private final UserService userService;

    /**
     * 공간 Space 생성
     *
     * "orgGuid": orgService.createOrg(org).getMetadata().getId()
     *
     * @param space
     * @return
     */
    @PostMapping(value = "/v3/spaces")
    public Map<?,?> createSpace(@RequestBody Space space, @RequestHeader(Common.AUTHORIZATION_HEADER_KEY) String token){
        Map<String, Object> result = new HashMap<>();
        String userId = userService.getUserIdByUsername("admin");

        result.put("result", spaceService.createSpace(space.getSpaceName(), space.getOrgGuid(), userId, token));
        return result;
    }


}
