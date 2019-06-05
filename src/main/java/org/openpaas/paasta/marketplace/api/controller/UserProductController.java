package org.openpaas.paasta.marketplace.api.controller;

import org.openpaas.paasta.marketplace.api.domain.Product;
import org.openpaas.paasta.marketplace.api.domain.UserProduct;
import org.openpaas.paasta.marketplace.api.domain.UserProductList;
import org.openpaas.paasta.marketplace.api.domain.UserProductSpecification;
import org.openpaas.paasta.marketplace.api.service.UserProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/user/product")
@Slf4j
public class UserProductController extends AbstractController {

	@Autowired
    UserProductService userProductService;

	/**
	 * 사용자 구매상품 목록 조회
	 * 
	 * @param spec
	 * @return
	 */
    @GetMapping
    public UserProductList getUserProductList(UserProductSpecification spec) {
        log.info("getUserProductList: spec={}", spec);

        return userProductService.getUserProductList(spec);
    }

    /**
     * 사용자 구매상품 상세 조회
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public UserProduct getUserProduct(@PathVariable("id") Long id) {
        log.info("getUserProduct: id={}", id);

        return userProductService.getUserProduct(id);
    }

    /**
     * 사용자 구매상품 등록
     * 
     * @param userProduct
     * @return
     */
    @PostMapping
    public UserProduct createUserProduct(@RequestBody Product product) {
        log.info("createUserProduct: softwareInstance={}", product);

        return userProductService.createUserProduct(product);
    }

    /**
     * 사용자 구매상품 수정
     * 
     * @param id
     * @return
     */
    @PutMapping("/{id}/{status}")
    public UserProduct updateUserProduct(@PathVariable("id") Long id, @RequestBody UserProduct userProduct) {
        log.info("updateUserProduct: id={}, status={}", id, userProduct.getProvisionStatus());
        
        return userProductService.updateUserProduct(id, userProduct);
    }

}
