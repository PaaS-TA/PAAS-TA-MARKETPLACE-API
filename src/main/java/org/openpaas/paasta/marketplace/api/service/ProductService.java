package org.openpaas.paasta.marketplace.api.service;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.common.CommonService;
import org.openpaas.paasta.marketplace.api.domain.Product;
import org.openpaas.paasta.marketplace.api.domain.ProductList;
import org.openpaas.paasta.marketplace.api.domain.ProductSpecification;
import org.openpaas.paasta.marketplace.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 상품 Service
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-06-03
 */
@Service
@Transactional
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommonService commonService;

    /**
     * 상품 목록 검색 조회
     *
     * @param spec the product specification object
     * @param pageable the pageable object
     * @return Page
     */
    public ProductList getProductList(ProductSpecification spec, Pageable pageable) {
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        log.info("  - PageNumber :: {}", pageable.getPageNumber());
        log.info("  - PageSize :: {}", pageable.getPageSize());
        log.info("  - Sort :: {}", pageable.getSort());
        log.info("  - Offset :: {}", pageable.getOffset());
        log.info("  - HasPrevious :: {}", pageable.hasPrevious());
        log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        ProductList productList;
        Page<Product> productListPage;

        productListPage = productRepository.findAll(spec, pageable);

        productList = (ProductList) commonService.setPageInfo(productListPage, new ProductList());
        productList.setItems(productListPage.getContent());

        return productList;
    }
}
