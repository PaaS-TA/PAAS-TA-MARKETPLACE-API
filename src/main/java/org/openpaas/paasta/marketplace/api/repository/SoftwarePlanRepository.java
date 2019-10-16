package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SoftwarePlanRepository extends JpaRepository<SoftwarePlan, Long>, JpaSpecificationExecutor<SoftwarePlan> {

    SoftwarePlan findByName(String name);

}
