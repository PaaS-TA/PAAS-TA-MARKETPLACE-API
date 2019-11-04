package org.openpaas.paasta.marketplace.api.repository;

import java.util.List;

import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstanceRepository extends JpaRepository<Instance, Long>, JpaSpecificationExecutor<Instance> {

	// 사용자의 전체 사용금액 계산
	@Query(value="SELECT  ROUND(SUM((sp.cpu_amt + sp.memory_amt + sp.disk_amt)/c.days)) AS usagePriceTotal\n"
	            +"FROM    instance i, software s, software_plan sp, calendar c\n"
	            +"WHERE   1=1\n"
	            +"AND     i.software_plan_id = sp.id\n"
	            +"AND     i.software_id = s.id\n"
	            +"AND     IFNULL(i.usage_end_date, STR_TO_DATE('2999-12', '%Y-%m')) >= DATE_FORMAT(STR_TO_DATE(:usageStartDate, '%Y-%m-%d'),'%Y-%m-%d:%H%i%S')\n"
	            +"AND     i.usage_start_date < DATE_ADD(DATE_FORMAT(STR_TO_DATE(:usageEndDate, '%Y-%m-%d'),'%Y-%m-%d:%H%i%S'), interval 1 month)\n"
	            +"AND     c.dt >= STR_TO_DATE(:usageStartDate, '%Y-%m-%d')\n"
	            +"AND     c.dt < DATE_FORMAT(DATE_ADD(DATE_FORMAT(STR_TO_DATE(:usageEndDate, '%Y-%m-%d'),'%Y-%m-%d:%H%i%S'), interval 1 month), '%Y%m%d')\n"
	            +"AND     c.dt BETWEEN DATE_FORMAT(i.usage_start_date, '%Y%m%d') AND IFNULL(DATE_FORMAT(i.usage_end_date, '%Y%m%d'), DATE_FORMAT(now(),'%Y%m%d'))\n"
	            +"AND     i.created_by = :userId\n"
	, nativeQuery=true)
    public Long usagePriceTotal(@Param("userId") String userId
    		, @Param("usageStartDate") String usageStartDate
			, @Param("usageEndDate") String usageEndDate);
	
	// 사용요금 계산
    @Query(value="SELECT  CONCAT(i.id) AS instanceId\n"
	    		+"        ,CONCAT(ROUND(SUM((sp.cpu_amt + sp.memory_amt + sp.disk_amt)/c.days))) AS usagePriceTotal\n"
	    		+"FROM    instance i, software s, software_plan sp, calendar c\n"
	    		+"WHERE   1=1\n"
	    		+"AND     i.software_plan_id = sp.id\n"
	    		+"AND     i.software_id = s.id\n"
	    		+"AND     IFNULL(i.usage_end_date, STR_TO_DATE('2999-12', '%Y-%m')) >= DATE_FORMAT(STR_TO_DATE(:usageStartDate, '%Y-%m-%d'),'%Y-%m-%d:%H%i%S')\n"
	    		+"AND     i.usage_start_date < DATE_ADD(DATE_FORMAT(STR_TO_DATE(:usageEndDate, '%Y-%m-%d'),'%Y-%m-%d:%H%i%S'), interval 1 month)\n"
	    		+"AND     c.dt >= STR_TO_DATE(:usageStartDate, '%Y-%m-%d')\n"
	    		+"AND     c.dt < DATE_FORMAT(DATE_ADD(DATE_FORMAT(STR_TO_DATE(:usageEndDate, '%Y-%m-%d'),'%Y-%m-%d:%H%i%S'), interval 1 month), '%Y%m%d')\n"
	    		+"AND     c.dt BETWEEN DATE_FORMAT(i.usage_start_date, '%Y%m%d') AND IFNULL(DATE_FORMAT(i.usage_end_date, '%Y%m%d'), DATE_FORMAT(now(),'%Y%m%d'))\n"
	    		+"AND     i.id IN :inInstanceId\n"
	    		+"GROUP BY i.id\n"
	, nativeQuery=true)
	public List<Object[]> pricePerInstanceList(@Param("inInstanceId") List<Long> inInstanceId
			, @Param("usageStartDate") String usageStartDate
			, @Param("usageEndDate") String usageEndDate);
 
}
