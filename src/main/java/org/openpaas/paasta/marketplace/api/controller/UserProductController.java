package org.openpaas.paasta.marketplace.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.domain.UserProduct;
import org.openpaas.paasta.marketplace.api.domain.UserProductList;
import org.openpaas.paasta.marketplace.api.domain.UserProductSpecification;
import org.openpaas.paasta.marketplace.api.service.UserProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ApiConstants.URI_API_USER_PRODUCT)
@Slf4j
public class UserProductController extends AbstractController {

    private static final int PAGE_SIZE = 8;

	@Autowired
    UserProductService userProductService;


    /**
     * 사용자 구매 상품 목록 검색 조회
     *
     * @param categoryId the category id
     * @param productName the product name
     * @param spec the user product specification object
     * @param pageable the pageable object
     * @return UserProductList
     */
    @GetMapping
    public UserProductList getUserProductList(@RequestParam(value = "userId", defaultValue = "") String userId,
                                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                                              @RequestParam(value = "productName", required = false) String productName,
                                              UserProductSpecification spec,
                                              @PageableDefault(size = PAGE_SIZE) Pageable pageable) {
        log.info("getUserProductList: spec={}, pageable={}", spec, pageable);

        spec.setUserId(userId);
        spec.setCategoryId(categoryId);
        spec.setProductName(productName);

        return userProductService.getUserProductList(spec, pageable);
    }

    /**
     * 사용자 구매 상품 상세 조회
     * @param id the id
     * @return UserProduct
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
