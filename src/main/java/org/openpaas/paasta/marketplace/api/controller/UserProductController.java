package org.openpaas.paasta.marketplace.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.domain.UserProduct;
import org.openpaas.paasta.marketplace.api.domain.UserProductList;
import org.openpaas.paasta.marketplace.api.domain.UserProductSpecification;
import org.openpaas.paasta.marketplace.api.service.UserProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ApiConstants.URI_API_USER_PRODUCT)
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

    @PostMapping("/{id}")
    public UserProduct provisionUserProduct(@PathVariable(value = "id") Long id){
        UserProduct result = null;

        try {
            //result = userProductService.provision(id).get();
        } catch (Exception e) {
            // ignore
        }

        return result;
    }

    /**
     * 사용자 구매상품 등록
     *
     * @param userProduct the user product
     * @return UserProduct
     */
    @PostMapping
    public UserProduct createUserProduct(@RequestBody UserProduct userProduct) {
        log.info("createUserProduct: productInstance={}", userProduct);

        return userProductService.createUserProduct(userProduct);
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
