package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CustomCode;
import org.openpaas.paasta.marketplace.api.domain.CustomCodeList;
import org.openpaas.paasta.marketplace.api.service.CustomCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Custom Code 관리 Controller
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-08
 */
@RestController
@RequestMapping(value = ApiConstants.URI_API_CUSTOM_CODE)
public class CustomCodeController {
	
    @Autowired
    private CustomCodeService customCodeService;

    /**
     * GroupCode 로 단위코드 목록 조회
     *
     * @param groupCode
     * @return CustomCodeList
     */
    @GetMapping(value = "/{groupCode}")
    public CustomCodeList getUnitCodeListByGroupCode(@PathVariable String groupCode) {
        return customCodeService.getUnitCodeListByGroupCode(groupCode.toUpperCase());
    }

    /**
     * 단위코드 상세 조회
     * 
     * @param groupCode
     * @param unitCode
     * @return CustomCode
     */
    @GetMapping(value = "/{groupCode}/{unitCode}")
    public CustomCode getUnitCode(@PathVariable String groupCode, @PathVariable String unitCode){
        return customCodeService.getUnitCode(groupCode.toUpperCase(), unitCode.toUpperCase());
    }

    /**
     * Custom Code 생성
     *
     * @param customCode the custom code
     * @return the CustomCode
     */
    @PostMapping
    public CustomCode createCustomCode(@RequestBody CustomCode customCode){
    	return customCodeService.createCustomCode(customCode);
    }


    /**
     * Custom Code 수정
     *
     * @param id the id
     * @param customCode the custom code
     * @return CustomCode
     */
    @PutMapping("/{id}")
    public CustomCode updateCustomCode(@PathVariable Long id, @RequestBody CustomCode customCode) {
        return customCodeService.updateCustomCode(id, customCode);
    }

}
