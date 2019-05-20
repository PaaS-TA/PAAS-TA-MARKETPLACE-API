package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.domain.CustomCode;
import org.openpaas.paasta.marketplace.api.service.CustomCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Custom Code 관리 Controller
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-08
 */
@RestController
@RequestMapping(value = "/customCode")
public class CustomCodeController {
	
    @Autowired
    private CustomCodeService customCodeService;

    /**
     * GroupCode 로 단위코드 목록 조회
     *
     * @param groupCode
     * @return List<CustomCode>
     */
    @GetMapping(value = "/{groupCode}")
    public List<CustomCode> getUnitCodeListByGroupCode(@PathVariable String groupCode){
        return customCodeService.getUnitCodeListByGroupCode(groupCode.toUpperCase());
    }

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

}
