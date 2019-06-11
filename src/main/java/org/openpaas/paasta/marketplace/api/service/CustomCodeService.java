package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.domain.CustomCode;
import org.openpaas.paasta.marketplace.api.domain.CustomCodeList;
import org.openpaas.paasta.marketplace.api.repository.CustomCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Custom Code 관리 Service
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-08
 */
@Service
@Transactional
public class CustomCodeService {

    @Autowired
    private CustomCodeRepository customCodeRepository;

    /**
     * GroupCode 로 단위코드 목록 조회
     *
     * @param groupCode the group code
     * @return CustomCodeList
     */
    public CustomCodeList getUnitCodeListByGroupCode(String groupCode) {
        List<CustomCode> codes = customCodeRepository.findAllByGroupCode(groupCode);
        CustomCodeList codeList = new CustomCodeList();
        codeList.setResultCode(ApiConstants.RESULT_STATUS_SUCCESS);
        codeList.setItems(codes);
        
        return codeList;
    }

    /**
     * GroupCode 와 UnitCode 로 단위코드 데이터 조회
     * 
     * @param groupCode the group code
     * @param unitCode the unit code
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

    /**
     * Custom Code 수정
     *
     * @param id the id
     * @param code the code
     * @return CustomCode
     */
    public CustomCode updateCustomCode(Long id, CustomCode code) {
    	CustomCode updCode = customCodeRepository.getOne(id);
    	updCode.setGroupCode(code.getGroupCode());
    	updCode.setGroupCodeName(code.getGroupCodeName());
    	updCode.setUnitCode(code.getUnitCode());
    	updCode.setUnitCodeName(code.getUnitCodeName());
//    	updCode.setDeleteYn(code.getDeleteYn());

    	return customCodeRepository.save(updCode);
    }

}
