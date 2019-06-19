package org.openpaas.paasta.marketplace.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.domain.Product;
import org.openpaas.paasta.marketplace.api.domain.ProductList;
import org.openpaas.paasta.marketplace.api.domain.ProductSpecification;
import org.openpaas.paasta.marketplace.api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * 상품 Controller
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-06-03
 */
@RestController
@RequestMapping(value = ApiConstants.URI_API_PRODUCT)
@Slf4j
public class ProductController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private ProductService productService;

    /**
     * 상품 목록 검색 조회
     *
     * @param categoryId the category id
     * @param productName the product name
     * @param spec the product specification object
     * @param pageable the pageable object
     * @return ProductList
     */
    @GetMapping
    public ProductList getProductList(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                      @RequestParam(value = "productName", required = false) String productName,
                                      ProductSpecification spec,
                                      @PageableDefault(size = PAGE_SIZE) Pageable pageable){
        log.info("getProductList: spec={}, pageable={}", spec, pageable);

        spec.setCategoryId((categoryId));
        spec.setProductName(productName);

        return productService.getProductList(spec, pageable);
    }


    /**
     * 상품 상세 조회
     *
     * @param id the id
     * @return Product
     */
    @GetMapping(value = "/{id}")
    public Product getProduct(@PathVariable(value = "id") Long id){
    	log.info("id: {}", id);
        return productService.getProduct(id);
    }


    /**
     * 상품 등록
     *
     * @param product the product
     * @return Product
     */
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        log.info("product={}", product);
        return productService.createProduct(product);
    }

    /**
     * 상품 수정
     * @param id
     * @param product
     * @return Product
     */
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
    	log.info("id: {}, product: {}", id, product);
        return productService.updateProduct(id, product);
    }

}
