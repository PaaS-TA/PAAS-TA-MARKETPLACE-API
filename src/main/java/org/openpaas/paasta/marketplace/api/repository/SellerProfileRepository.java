package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
}
