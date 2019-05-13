package org.openpaas.paasta.marketplace.api.service;

import org.openpaas.paasta.marketplace.api.domain.CustomCode;
import org.openpaas.paasta.marketplace.api.repository.CustomCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom Code 관리 Service
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-08
 */
@Service
public class CustomCodeService {

    @Autowired
    private CustomCodeRepository customCodeRepository;

    /**
     * Custom Code 생성
     *
     * @param customCode the custom code
     * @return the CustomCode
     */
    public CustomCode createCustomCode(CustomCode customCode) {
        return customCodeRepository.save(customCode);
    }

    /**
     * Group Type Name 으로 Group Code 목록 조회
     *
     * @param groupTypeName the group type name
     * @return List<CustomCode>
     */
    public List<CustomCode> getGroupCodeListByGroupName(String groupTypeName) {
        return customCodeRepository.findByGroupCode(groupTypeName);
    }
}
