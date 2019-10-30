package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstanceRepository extends JpaRepository<Instance, Long>, JpaSpecificationExecutor<Instance> {

	@Query(value="SELECT  SUM(ROUND((t.pricePerMonth/t.lastDay) * t.usingDay)) AS priceTotal\n"
				+"FROM    (\n"
	            +"			SELECT  DATE_FORMAT(LAST_DAY(NOW()), '%d') AS lastDay\n"
                +"					,TIMESTAMPDIFF(DAY, usage_start_date, NOW()) + 2 AS usingDay\n"
                +"					,IFNULL(sp.cpu_amt,0) + IFNULL(sp.memory_amt,0) + IFNULL(sp.disk_amt,0) AS pricePerMonth\n"
	            +"			FROM    instance it\n"
	            +"					INNER JOIN software_plan sp\n"
	            +"						ON (sp.id = it.software_plan_id)\n"
	            +"			WHERE   it.created_by = :userId\n"
	            +"		) t\n"
	, nativeQuery=true)
    public Long usagePriceTotal(@Param("userId") String userId);
	
}
