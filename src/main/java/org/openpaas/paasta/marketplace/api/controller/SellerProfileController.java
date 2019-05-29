package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.domain.SellerProfile;
import org.openpaas.paasta.marketplace.api.domain.SellerProfileList;
import org.openpaas.paasta.marketplace.api.service.SellerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * 판매자 프로필 Controller
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */

@RestController
@RequestMapping(value = ApiConstants.URI_API_SELLER_PROFILE)
@Slf4j
public class SellerProfileController {

    @Autowired
    private SellerProfileService sellerProfileService;
    
    /**
     * 판매자 프로필 목록 조회
     *
     * @return List
     */
//    @GetMapping
//    public List<SellerProfile> getSellerProfileList() {
//        return sellerProfileService.getSellerProfileList();
//    }
    @GetMapping
    public SellerProfileList getSellerProfileList() {
        return sellerProfileService.getSellerProfileList();
    }

    /**
     * 판매자 프로필 상세 조회
     *
     * @param id the id
     * @return SellerProfile
     */
    @GetMapping("/{id}")
    public SellerProfile getSellerProfile(@PathVariable Long id) {
        return sellerProfileService.getSellerProfile(id);
    }

    /**
     * 판매자 프로필 등록
     *
     * @param sellerProfile
     * @return SellerProfile
     */
    @PostMapping
    public SellerProfile createSellerProfile(@RequestBody SellerProfile sellerProfile) {
    	log.info("seller: " + sellerProfile.toString());
    	return sellerProfileService.createSellerProfile(sellerProfile);
    }


    /**
     * 판매자 프로필 수정
     *
     * @param id the id
     * @param sellerProfile the seller profile
     * @return SellerProfile
     */
    @PutMapping("/{id}")
    public SellerProfile updateSellerProfile(@PathVariable Long id, @RequestBody SellerProfile sellerProfile) {
    	log.info("seller: " + sellerProfile.toString());
        return sellerProfileService.updateSellerProfile(id, sellerProfile);
    }
}
