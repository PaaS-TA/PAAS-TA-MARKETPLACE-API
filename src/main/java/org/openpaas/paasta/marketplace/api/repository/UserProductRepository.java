package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Long>, JpaSpecificationExecutor<UserProduct> {

}
