package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SoftwareRepository extends JpaRepository<Software, Long>, JpaSpecificationExecutor<Software> {

    Software findByName(String name);

    @Modifying
    @Query("UPDATE Software s SET s.category = NULL WHERE s.category.id = :categoryId")
    int clearCategory(@Param("categoryId") Long categoryId);

}
