package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SoftwareRepository extends JpaRepository<Software, Long>, JpaSpecificationExecutor<Software> {

    Software findByName(String name);

    @Modifying
    @Query("UPDATE Software s SET s.category = NULL WHERE s.category.id = :categoryId")
    int clearCategory(@Param("categoryId") Long categoryId);

    List<Software> findByCreatedBy(String providerId);
    
    @Query(value="SELECT  COUNT(0) AS soldSwCount \n"
	    		+"FROM    ( \n"
	    		+"            SELECT  it.software_id \n"
	    		+"            FROM    software so \n"
	    		+"                    INNER JOIN instance it \n"
	    		+"                        ON (it.software_id = so.id) \n"
	    		+"            WHERE   1=1 \n"
	    		+"            AND     so.status = :status \n"
	    		+"            AND     so.created_by = :userId \n"
	    		+"            GROUP BY it.software_id \n"
	    		+"        ) t \n"
	, nativeQuery=true)
    public Integer getSoldSoftwareCount(@Param("userId") String userId, @Param("status") String status);
}
