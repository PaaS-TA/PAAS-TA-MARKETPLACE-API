package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.SoftwarePlanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SoftwarePlanHistoryRepository extends JpaRepository<SoftwarePlanHistory, Long>, JpaSpecificationExecutor<SoftwarePlanHistory> {

}
