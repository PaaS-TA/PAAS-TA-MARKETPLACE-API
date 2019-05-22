package org.openpaas.paasta.marketplace.api.repository;

import java.util.List;

import org.openpaas.paasta.marketplace.api.domain.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 판매자 프로필 Repository
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {

	List<SellerProfile> findAllByDeleteYn(String deleteYn);

	SellerProfile getOneByIdAndDeleteYn(Long id, String deleteYn);

}
