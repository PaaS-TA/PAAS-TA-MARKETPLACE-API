package org.openpaas.paasta.marketplace.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.common.CommonService;
import org.openpaas.paasta.marketplace.api.domain.Product;
import org.openpaas.paasta.marketplace.api.domain.ProductList;
import org.openpaas.paasta.marketplace.api.domain.ProductSpecification;
import org.openpaas.paasta.marketplace.api.domain.Screenshot;
import org.openpaas.paasta.marketplace.api.repository.CategoryRepository;
import org.openpaas.paasta.marketplace.api.repository.ProductRepository;
import org.openpaas.paasta.marketplace.api.repository.SellerProfileRepository;
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
	private CommonService commonService;
	
	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private SellerProfileRepository sellerProfileRepository;

	@Autowired
    private ProductRepository productRepository;

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

    /**
     * 상품 상세 조회
     *
     * @param id the id
     * @return Product
     */
    public Product getProduct(Long id) {
    	Product product = new Product();
    	Optional<Product> result = productRepository.findById(id);
    	if (result.isPresent()) {
    		product = (Product) commonService.setResultModel(result.get(), ApiConstants.RESULT_STATUS_SUCCESS);
    	} else {
    		product.setResultCode(ApiConstants.RESULT_STATUS_FAIL);
    		product.setResultMessage("Product Not Found");
    	}
    	return product;
    }
    
    /**
     * 상품 등록
     * @param product
     * @return Product
     */
    public Product createProduct(Product product) {
    	String userId = product.getCreateId();
    	// 카테고리
    	product.setCategory(categoryRepository.getOneByIdAndDeleteYn(product.getCategoryId(), ApiConstants.DELETE_YN_N));
    	// 판매자
    	product.setSeller(sellerProfileRepository.getOneBySellerIdAndDeleteYn(product.getSellerId(), ApiConstants.DELETE_YN_N));
		// 스크린샷파일
		List<String> screenshotFileNames = product.getScreenshotFileNames();
		List<Screenshot> screenshots = new ArrayList<Screenshot>();
		for (String screenshotFileName : screenshotFileNames) {
			Screenshot screenshot = new Screenshot();
			screenshot.setScreenshotFileName(screenshotFileName);
			screenshot.setCreateId(userId);
			screenshot.setUpdateId(userId);
			screenshots.add(screenshot);
		}
		product.setScreenshots(screenshots);

		return (Product) commonService.setResultModel(productRepository.save(product), ApiConstants.RESULT_STATUS_SUCCESS);
    }
    
    /**
     * 상품 수정
     * @param product
     * @return
     */
    public Product updateProduct(Long id, Product updProduct) {
    	String userId = updProduct.getUpdateId();
    	// 상품데이터 조회
    	Product product = getProduct(id);

    	// 카테고리
    	if (null != updProduct.getCategoryId() && updProduct.getCategoryId() > 0L) {
    		product.setCategory(categoryRepository.getOneByIdAndDeleteYn(updProduct.getCategoryId(), ApiConstants.DELETE_YN_N));
    	}
    	// 상품 개요
    	if (null != updProduct.getSimpleDescription()) {
    		product.setSimpleDescription(updProduct.getSimpleDescription());
    	}
    	// 상품 상세
    	if (null != updProduct.getDetailDescription()) {
    		product.setDetailDescription(updProduct.getDetailDescription());
    	}
    	// 미터링 금액
    	if (updProduct.getUnitPrice() > 0) {
    		product.setUnitPrice(updProduct.getUnitPrice());
    	}
    	// 전시여부
    	if (null != updProduct.getDisplayYn()) {
    		product.setDisplayYn(updProduct.getDisplayYn());
    	}
    	// 아이콘파일
    	if (null != updProduct.getIconFileName()) {
    		product.setIconFileName(updProduct.getIconFileName());
    	}
		// 스크린샷파일
    	if (null != updProduct.getScreenshotFileNames() && updProduct.getScreenshotFileNames().size() > 0) {
	    	//=> 기존 데이터 모두 삭제
    		List<Screenshot> list = product.getScreenshots();
    		for (Screenshot item : list) {
    			item.setDeleteYn(ApiConstants.DELETE_YN_Y);
    		}
    		product.setScreenshots(list);
	    	productRepository.save(product);
	    	//=> 신규 데이터 등록
			List<String> screenshotFileNames = updProduct.getScreenshotFileNames();
			List<Screenshot> screenshots = new ArrayList<Screenshot>();
			for (String screenshotFileName : screenshotFileNames) {
				Screenshot screenshot = new Screenshot();
				screenshot.setScreenshotFileName(screenshotFileName);
				screenshot.setCreateId(userId);
				screenshot.setUpdateId(userId);
				screenshots.add(screenshot);
			}
			product.setScreenshots(screenshots);
    	}

		return (Product) commonService.setResultModel(productRepository.save(product), ApiConstants.RESULT_STATUS_SUCCESS);
    }

}
