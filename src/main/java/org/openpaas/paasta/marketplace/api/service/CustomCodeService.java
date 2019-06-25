package org.openpaas.paasta.marketplace.api.service;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.common.CommonService;
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
	private CommonService commonService;
	
    @Autowired
    private CustomCodeRepository customCodeRepository;

    /**
     * GroupCode 로 단위코드 목록 조회
     *
     * @param groupCode the group code
     * @return CustomCodeList
     */
    public CustomCodeList getCodeListByGroupCode(String groupCode) {
        CustomCodeList customCodeList = new CustomCodeList();
        customCodeList.setItems(customCodeRepository.findAllByGroupCode(groupCode));

        return (CustomCodeList) commonService.setResultModel(customCodeList, ApiConstants.RESULT_STATUS_SUCCESS);
    }

    /**
     * GroupCode 와 UnitCode 로 단위코드 데이터 조회
     * 
     * @param groupCode the group code
     * @param unitCode the unit code
     * @return CustomCode
     */
    public CustomCode getCodeByGroupCodeAndUnitCode(String groupCode, String unitCode) {
        return (CustomCode) commonService.setResultModel(customCodeRepository.findByGroupCodeAndUnitCode(groupCode, unitCode), ApiConstants.RESULT_STATUS_SUCCESS);
    }
    
    /**
     * UnitCode 로 단위코드 데이터 조회
     *
     * @param unitCode the unit code
     * @return CustomCode
     */
    public CustomCode getCodeByUnitCode(String unitCode) {
    	return (CustomCode) commonService.setResultModel(customCodeRepository.findByUnitCode(unitCode), ApiConstants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ID로 단위코드 데이터 조회
     * @param id the id
     * @return CustomCode
     */
    public CustomCode getCodeById(Long id) {
    	return (CustomCode) commonService.setResultModel(customCodeRepository.getOne(id), ApiConstants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Custom Code 생성
     *
     * @param customCode the custom code
     * @return the CustomCode
     */
    public CustomCode createCustomCode(CustomCode customCode) {
        return (CustomCode) commonService.setResultModel(customCodeRepository.save(customCode), ApiConstants.RESULT_STATUS_SUCCESS);
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

    	return (CustomCode) commonService.setResultModel(customCodeRepository.save(updCode), ApiConstants.RESULT_STATUS_SUCCESS);
    }

}
