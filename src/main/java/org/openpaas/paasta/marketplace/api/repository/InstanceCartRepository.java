package org.openpaas.paasta.marketplace.api.repository;

import java.util.List;

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
    
    @Transactional
    @Modifying
    @Query("delete from InstanceCart ic where ic.createdBy = :userId and id in :inInstanceCartId ")
    Integer deleteByInstanceCartIdListInQuery(@Param("userId") String userId, @Param("inInstanceCartId") List<Long> inInstanceCartId);
    
    default Integer allDelete(String userId) {
        return deleteAllByUserIdInQuery(userId);
    }
    
    default Integer delete(String userId, List<Long> inInstanceCartId) {
    	return deleteByInstanceCartIdListInQuery(userId, inInstanceCartId);
    }
}
