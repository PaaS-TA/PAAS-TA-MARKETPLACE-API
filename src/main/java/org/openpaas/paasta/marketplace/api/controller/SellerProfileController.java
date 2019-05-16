package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.domain.SellerProfile;
import org.openpaas.paasta.marketplace.api.service.SellerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 판매자 프로필 Controller
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

    /**
     * 판매자 프로필 등록
     *
     * @param sellerProfile the seller profile
     * @return SellerProfile
     */
    @PostMapping
    public SellerProfile createProfile(@RequestBody SellerProfile sellerProfile){
        return sellerProfileService.createProfile(sellerProfile);
    }

    /**
     * 판매자 프로필 상세 조회
     *
     * @param id the id
     * @return SellerProfile
     */
    @GetMapping("/{id}")
    public SellerProfile getProfile(@PathVariable Long id){
        return sellerProfileService.getProfile(id);
    }


    /**
     * 판매자 프로필 수정
     *
     * @param sellerProfile the seller profile
     * @return SellerProfile
     */
    @PutMapping("/{id}")
    public SellerProfile updateProfile(@PathVariable Long id, @RequestBody SellerProfile sellerProfile) {
        sellerProfile.setId(id);
        return sellerProfileService.updateProfile(sellerProfile);
    }
}
