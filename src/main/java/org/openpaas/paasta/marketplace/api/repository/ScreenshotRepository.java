package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.model.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long>, JpaSpecificationExecutor<Screenshot> {

}
