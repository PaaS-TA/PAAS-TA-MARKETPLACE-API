package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.model.Space;
import org.openpaas.paasta.marketplace.api.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Space Controller
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-04-03
 */
@RestController
@RequestMapping(value = "/spaces")
public class SpaceController {
    private static final String CF_AUTHORIZATION_HEADER_KEY = "cf-Authorization";

    @Autowired
    private SpaceService spaceService;

    /**
     * Space 생성
     *
     * @param space the space
     * @param token the token
     * @return Space
     */
    @PostMapping
    public Space createSpace(@RequestBody Space space,
                             @RequestHeader(CF_AUTHORIZATION_HEADER_KEY) String token){
        return spaceService.createSpace(space, token);
    }
}
