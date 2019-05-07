package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.model.SellerProfile;
import org.openpaas.paasta.marketplace.api.service.SellerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */

@RestController
@RequestMapping(value = "/profile")
public class SellerProfileController {

    @Autowired
    private SellerProfileService sellerProfileService;

    @PostMapping
    public SellerProfile createProfile(@RequestBody SellerProfile sellerProfile){
        return sellerProfileService.createProfile(sellerProfile);
    }
}
