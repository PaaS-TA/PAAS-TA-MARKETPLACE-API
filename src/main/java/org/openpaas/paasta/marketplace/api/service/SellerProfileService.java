package org.openpaas.paasta.marketplace.api.service;

import org.openpaas.paasta.marketplace.api.domain.SellerProfile;
import org.openpaas.paasta.marketplace.api.repository.SellerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 판매자 프로필 Service
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */

@Service
public class SellerProfileService {
    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    /**
     * 판매자 프로필 등록
     *
     * @param sellerProfile the seller profile
     * @return SellerProfile
     */
    public SellerProfile createProfile(SellerProfile sellerProfile) {
        return sellerProfileRepository.save(sellerProfile);
    }

    /**
     * 판매자 프로필 상세 조회
     *
     * @param id the id
     * @return SellerProfile
     */
    public SellerProfile getProfile(Long id) {
        return sellerProfileRepository.getOne(id);
    }
}
