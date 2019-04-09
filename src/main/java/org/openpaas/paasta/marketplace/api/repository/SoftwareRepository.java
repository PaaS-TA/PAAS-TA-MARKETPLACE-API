package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.model.Software;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SoftwareRepository extends JpaRepository<Software, Long>, JpaSpecificationExecutor<Software> {

}
