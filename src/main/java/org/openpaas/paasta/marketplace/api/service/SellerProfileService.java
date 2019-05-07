package org.openpaas.paasta.marketplace.api.service;

import org.openpaas.paasta.marketplace.api.model.SellerProfile;
import org.openpaas.paasta.marketplace.api.repository.SellerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */

@Service
public class SellerProfileService {
    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    public SellerProfile createProfile(SellerProfile sellerProfile) {
        return sellerProfileRepository.save(sellerProfile);
    }
}
