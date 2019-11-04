package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.TestSoftwareInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-10-31
 */
public interface TestSoftwareInfoRepository extends JpaRepository<TestSoftwareInfo, Long>, JpaSpecificationExecutor<TestSoftwareInfo> {
    List<TestSoftwareInfo> findBySoftwareId(Long id);
}
