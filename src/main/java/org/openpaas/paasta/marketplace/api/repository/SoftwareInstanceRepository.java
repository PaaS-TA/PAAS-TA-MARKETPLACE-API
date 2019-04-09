package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.model.SoftwareInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftwareInstanceRepository extends JpaRepository<SoftwareInstance, Long>, JpaSpecificationExecutor<SoftwareInstance> {

}
