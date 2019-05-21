package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

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

    private String deleteYn = "N";

    /**
     * 판매자 프로필 목록 조회
     *
     * @return List
     */
    public List<SellerProfile> getSellerProfileList() {
        return sellerProfileRepository.findAllByDeleteYn(deleteYn);
    }

    /**
     * 판매자 프로필 상세 조회
     *
     * @param id the id
     * @return SellerProfile
     */
    public SellerProfile getSellerProfile(String id) {
        return sellerProfileRepository.getOneByIdAndDeleteYn(id, deleteYn);
    }

    /**
     * 판매자 프로필 등록
     *
     * @param sellerProfile the seller profile
     * @return SellerProfile
     */
    public SellerProfile createSellerProfile(SellerProfile sellerProfile) {
    	return sellerProfileRepository.save(sellerProfile);
    }

    /**
     * 판매자 프로필 수정
     *
     * @param sellerProfile the seller profile
     * @return SellerProfile
     */
    public SellerProfile updateSellerProfile(SellerProfile sellerProfile) {
        return sellerProfileRepository.save(sellerProfile);
    }

    public SellerProfile getProfileByUserId(String userId) {
        return sellerProfileRepository.findByUserId(userId);
    }
}
