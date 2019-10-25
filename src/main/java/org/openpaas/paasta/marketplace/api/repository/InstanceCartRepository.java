package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.InstanceCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstanceCartRepository  extends JpaRepository<InstanceCart, Long>, JpaSpecificationExecutor<InstanceCart> {

}
