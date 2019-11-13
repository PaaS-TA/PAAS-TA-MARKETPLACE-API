package org.openpaas.paasta.marketplace.api.repository;

import java.util.List;

import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftwarePlanRepository extends JpaRepository<SoftwarePlan, Long>, JpaSpecificationExecutor<SoftwarePlan> {

    SoftwarePlan findByName(String name);
    SoftwarePlan findBySoftwareId(Long softwareId);

    @Query(value="SELECT  IFNULL(cpu_amt,0) + IFNULL(memory_amt,0) + IFNULL(disk_amt,0) AS price_per_month\n"
	    		+"FROM    software_plan\n"
	    		+"WHERE   1=1\n"
	    		+"AND     software_id = :softwareId\n"
	    		+"AND     id = :softwarePlaneId\n"
    , nativeQuery=true)
    public Long pricePerMonth(@Param("softwareId") String softwareId, @Param("softwarePlaneId") String softwarePlaneId);
    
    @Query(value="SELECT  sp.*\n"
	    		+"FROM    software_plan sp\n"
	    		+"WHERE   1=1\n"
	    		+"AND     in_use = 'Y'\n"
	    		+"AND     software_id = :softwareId\n"
	    		+"AND     apply_month = (\n"
	    		+"                        SELECT  MAX(apply_month) AS applyMonth\n"
	    		+"                        FROM    software_plan\n"
	    		+"                        WHERE   1=1\n"
	    		+"                        AND     in_use = 'Y'\n"
	    		+"                        AND     software_id = :softwareId\n"
	    		+"                        AND     apply_month <= DATE_FORMAT(LAST_DAY(NOW()), '%Y%m')\n"
	    		+"                      )\n"
    , nativeQuery=true)
    public List<SoftwarePlan> findCurrentSoftwarePlanList(@Param("softwareId") Long softwareId);
    
    @Query(value="SELECT  MIN(IFNULL(cpu_amt,0) + IFNULL(memory_amt,0) + IFNULL(disk_amt,0)) AS minPricePerMonth\n"
	    		+"FROM    software_plan sp\n"
	    		+"WHERE   1=1\n"
	    		+"AND     in_use = 'Y'\n"
	    		+"AND     software_id = :softwareId\n"
	    		+"AND     apply_month = (\n"
	    		+"                        SELECT  MAX(apply_month) AS applyMonth\n"
	    		+"                        FROM    software_plan\n"
	    		+"                        WHERE   1=1\n"
	    		+"                        AND     in_use = 'Y'\n"
	    		+"                        AND     software_id = :softwareId\n"
	    		+"                        AND     apply_month <= DATE_FORMAT(LAST_DAY(NOW()), '%Y%m')\n"
	    		+"                      )\n"
    , nativeQuery=true)
    public Long minPricePerMonth(@Param("softwareId") String softwareId);
    
    @Query(value="SELECT  CONCAT(id) \n"
    			+"	  	  ,CONCAT(IFNULL(cpu_amt,0) + IFNULL(memory_amt,0) + IFNULL(disk_amt,0)) AS price_per_month \n"
	    		+"FROM    software_plan \n"
	    		+"WHERE   1=1 \n"
	    		+"AND     id IN :inSoftwarePlanId \n"
	, nativeQuery=true)
	public List<Object[]> pricePerMonthList(@Param("inSoftwarePlanId") List<Long> inSoftwarePlanId);

}
