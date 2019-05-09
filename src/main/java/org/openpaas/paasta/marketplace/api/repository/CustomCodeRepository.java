package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.model.CustomCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Custom Code Repository
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-08
 */

@Repository
public interface CustomCodeRepository extends JpaRepository<CustomCode, Long> {

    // GroupTypeName 으로 Group Code 목록 조회
    List<CustomCode> findByGroupCode(String groupTypeName);
}
