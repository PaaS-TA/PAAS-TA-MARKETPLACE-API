package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.SoftwareHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SoftwareHistoryRepository extends JpaRepository<SoftwareHistory, Long>, JpaSpecificationExecutor<SoftwareHistory> {

}
