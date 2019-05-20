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
     * GroupCode 로 단위코드 목록 조회
     *
     * @param groupCode
     * @return List<CustomCode>
     */
    public List<CustomCode> getUnitCodeListByGroupCode(String groupCode) {
        return customCodeRepository.findByGroupCode(groupCode);
    }

    /**
     * GroupCode 와 UnitCode 로 단위코드 데이터 조회
     * 
     * @param groupCode
     * @param unitCode
     * @return CustomCode
     */
    public CustomCode getUnitCode(String groupCode, String unitCode) {
        return customCodeRepository.findByGroupCodeAndUnitCode(groupCode, unitCode);
    }

    /**
     * Custom Code 생성
     *
     * @param customCode the custom code
     * @return the CustomCode
     */
    public CustomCode createCustomCode(CustomCode customCode) {
        return customCodeRepository.save(customCode);
    }

}
