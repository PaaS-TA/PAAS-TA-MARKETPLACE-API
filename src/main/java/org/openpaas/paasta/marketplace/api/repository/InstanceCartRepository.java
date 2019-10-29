package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.InstanceCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InstanceCartRepository  extends JpaRepository<InstanceCart, Long>, JpaSpecificationExecutor<InstanceCart> {

    @Transactional
    @Modifying
    @Query("delete from InstanceCart ic where ic.createdBy = :userId")
    Integer deleteAllByUserIdInQuery(@Param("userId") String userId);
    
    default Integer allDelete(String userId) {
        return deleteAllByUserIdInQuery(userId);
    }
}
