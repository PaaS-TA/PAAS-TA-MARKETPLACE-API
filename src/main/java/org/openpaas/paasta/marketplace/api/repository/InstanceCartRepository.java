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
    
	@Query(value="SELECT  CONCAT(ic.id)  AS cartId \n"
				+"        ,CONCAT(so.id) AS softwareId \n"
				+"        ,so.name       AS softwareName \n"
				+"        ,so.version    AS softwareVersion \n"
				+"        ,CONCAT(ct.id) AS categoryId \n"
				+"        ,ct.name       AS categoryName \n"
				+"        ,CONCAT(IFNULL(sp.cpu_amt,0)+IFNULL(sp.memory_amt,0)+IFNULL(sp.disk_amt,0)) AS softwarePlanAmtMonth \n"
				+"        ,CONCAT(ROUND(SUM((IFNULL(sp.cpu_amt,0)+IFNULL(sp.memory_amt,0)+IFNULL(sp.disk_amt,0))/cl.days))) AS pricePerInstance \n"
				+"FROM    instance_cart ic \n"
				+"        INNER JOIN software so \n"
				+"            ON (so.id = ic.software_id) \n"
				+"        INNER JOIN software_plan sp \n"
				+"            ON (sp.id = ic.software_plan_id) \n"
				+"        INNER JOIN category ct \n"
				+"            ON (ct.id = so.category_id) \n"
				+"        CROSS JOIN calendar cl \n"
				+"WHERE   1=1 \n"
				+"AND     cl.dt BETWEEN DATE_FORMAT(IF(ic.usage_start_date >= :usageStartDate, ic.usage_start_date, :usageStartDate),'%Y%m%d') \n"
				+"                  AND DATE_FORMAT(IFNULL(ic.usage_end_date, :usageEndDate),'%Y%m%d') \n"
				+"AND     ic.created_by = :userId \n"
				+"GROUP BY ic.id \n"
		, nativeQuery=true)
	public List<Object[]> userAllCartList(@Param("userId") String userId
											 , @Param("usageStartDate") String usageStartDate
											 , @Param("usageEndDate") String usageEndDate);

    default Integer allDelete(String userId) {
        return deleteAllByUserIdInQuery(userId);
    }
    
    default Integer delete(String userId, List<Long> inInstanceCartId) {
    	return deleteByInstanceCartIdListInQuery(userId, inInstanceCartId);
    }
}
