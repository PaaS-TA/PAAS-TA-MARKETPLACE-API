package org.openpaas.paasta.marketplace.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.domain.Product;
import org.openpaas.paasta.marketplace.api.domain.UserProduct;
import org.openpaas.paasta.marketplace.api.domain.UserProductList;
import org.openpaas.paasta.marketplace.api.domain.UserProductSpecification;
import org.openpaas.paasta.marketplace.api.repository.ProductRepository;
import org.openpaas.paasta.marketplace.api.repository.UserProductRepository;
import org.openpaas.paasta.marketplace.api.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserProductService {

	@Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserProductRepository userProductRepository;

    public UserProductList getUserProductList(UserProductSpecification spec) {
    	List<UserProduct> userProducts = userProductRepository.findAll(spec);

    	UserProductList userProductList = new UserProductList();
    	userProductList.setResultCode(ApiConstants.RESULT_STATUS_SUCCESS);
    	userProductList.setItems(userProducts);

        return userProductList;
    }
    
    public UserProduct getUserProduct(Long id) {
    	return userProductRepository.findById(id).orElse(null);
    }
    
    public UserProduct createUserProduct(Product product) {
    	UserProduct userProduct = new UserProduct();

    	Product buyProduct = productRepository.findById(product.getId()).orElse(null);
    	userProduct.setProductName(product.getProductName());
    	userProduct.setMeteringType(product.getMeteringType());
    	userProduct.setUnitPrice(product.getUnitPrice());
    	userProduct.setUseEnddate(LocalDateTime.parse(DateUtils.END_DATE));

    	List<Product> products = new ArrayList<Product>();
        products.add(buyProduct);
        userProduct.setProducts(products);

        UserProduct saved = userProductRepository.save(userProduct);

        return saved;
    }

    public UserProduct updateUserProduct(Long id, UserProduct updData) {
        UserProduct userProduct = userProductRepository.findById(id).orElse(null);
        String provisionStatus = updData.getProvisionStatus().toUpperCase();
        switch (provisionStatus) {
        case ApiConstants.PROVISION_STATUS_READY:
        case ApiConstants.PROVISION_STATUS_INPROGRESS:
        	userProduct.setProvisionStatus(provisionStatus);
        	break;
        case ApiConstants.PROVISION_STATUS_SUCCESS:
        	userProduct.setProvisionStatus(provisionStatus);
        	userProduct.setAccessUrl(updData.getAccessUrl());
        	break;
        case ApiConstants.PROVISION_STATUS_FAIL:
        case ApiConstants.PROVISION_STATUS_STOP:
        	userProduct.setProvisionStatus(provisionStatus);
        	userProduct.setUseEnddate(LocalDateTime.now());
        	break;
        default:
        	userProduct.setProvisionStatus(ApiConstants.PROVISION_STATUS_FAIL);
        	userProduct.setUseEnddate(LocalDateTime.now());
        	break;
        }

        return userProduct;
    }

}
