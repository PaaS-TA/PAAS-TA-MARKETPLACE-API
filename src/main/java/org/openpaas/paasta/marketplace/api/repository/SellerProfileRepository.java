package org.openpaas.paasta.marketplace.api.repository;

import java.util.List;

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
public interface SellerProfileRepository extends JpaRepository<SellerProfile, String> {
	
	List<SellerProfile> findAllByDeleteYn(String deleteYn);
	
	SellerProfile getOneByIdAndDeleteYn(String id, String deleteYn);
	
}
