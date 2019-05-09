package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.model.CustomCode;
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
     * Group Type Name 으로 Group Code 목록 조회
     *
     * @param groupTypeName the group type name
     * @return List<CustomCode>
     */
    @GetMapping(value = "/{groupTypeName}")
    public List<CustomCode> getGroupCodeListByGroupName(@PathVariable String groupTypeName){
        return customCodeService.getGroupCodeListByGroupName(groupTypeName);
    }
}
