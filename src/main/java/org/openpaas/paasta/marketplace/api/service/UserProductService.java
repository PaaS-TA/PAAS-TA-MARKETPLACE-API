package org.openpaas.paasta.marketplace.api.service;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.common.CommonService;
import org.openpaas.paasta.marketplace.api.common.RestTemplateService;
import org.openpaas.paasta.marketplace.api.domain.Product;
import org.openpaas.paasta.marketplace.api.domain.UserProduct;
import org.openpaas.paasta.marketplace.api.domain.UserProductList;
import org.openpaas.paasta.marketplace.api.domain.UserProductSpecification;
import org.openpaas.paasta.marketplace.api.repository.ProductRepository;
import org.openpaas.paasta.marketplace.api.repository.UserProductRepository;
import org.openpaas.paasta.marketplace.api.util.NameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class UserProductService {
    @Value("${provisioning.try-count}")
    private int provisioningTryCount;

    @Value("${provisioning.timeout}")
    private long provisioningTimeout;

    @Value("${deprovisioning.try-count}")
    private int deprovisioningTryCount;

    @Value("${deprovisioning.timeout}")
    private long deprovisioningTimeout;

    @Autowired
    private CommonService commonService;

	@Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserProductRepository userProductRepository;

    @Autowired
    private RestTemplateService restTemplateService;


    /**
     * 사용자 구매 상품 목록 검색 조회
     *
     * @param spec the user product specification object
     * @param pageable the pageable object
     * @return UserProductList
     */
    public UserProductList getUserProductList(UserProductSpecification spec, Pageable pageable) {
        UserProductList userProductList;
        Page<UserProduct> userProductListPage;

        userProductListPage = userProductRepository.findAll(spec, pageable);

        userProductList = (UserProductList) commonService.setPageInfo(userProductListPage, new UserProductList());
        userProductList.setItems(userProductListPage.getContent());

        return (UserProductList) commonService.setResultModel(userProductList, ApiConstants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 사용자 구매 상품 상세 조회
     *
     * @param id the id
     * @return UserProduct
     */
    public UserProduct getUserProduct(Long id) {
    	return (UserProduct) commonService.setResultModel(userProductRepository.findById(id).orElse(null), ApiConstants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 사용자 구매상품 등록
     *
     * @param userProduct the user product
     * @return UserProduct
     */
    public UserProduct createUserProduct(UserProduct userProduct) {

        // 1. 상품 이름 새롭게 만들어줘야 함.
        // 2. 구매한 상품 정보 저장해야 함.
        // 3. CF API 호출해서 app push 해야 함.

    	Product buyProduct = productRepository.findById(userProduct.getProductId()).orElse(null);

        // 구매 후 생성할 application 의 새 이름
        String uuid = NameUtils.makeUniqueName();

    	userProduct.setProductName(buyProduct.getProductName());
    	userProduct.setUuid(uuid);
    	userProduct.setProduct(buyProduct);
    	userProduct.setCreateId(userProduct.getUserId());
    	userProduct.setUpdateId(userProduct.getUserId());
    	userProduct.setMeteringType(buyProduct.getMeteringType());
    	userProduct.setUnitPrice(buyProduct.getUnitPrice());
    	userProduct.setUseEnddate(LocalDateTime.now());

        return userProductRepository.save(userProduct);

    }


    // 실제로 CF 에 앱 푸시 및 실행하는 로직
//    public void provisionUserProduct(UserProduct userProduct) {
//
//
//
//
//        PaasSoftware paasSoftware = (PaasSoftware) softwareInstance.getSoftware();
//        String name = generateName(softwareInstance);
//
//        Application createdApplication = paasApplicationService.createPaasApplication(parseCatalog(userProduct));
//        paasApplicationService.initEnv(createdApplication.getGuid(), paasSoftware.getEnv());
//
//        String appGuid = createdApplication.getGuid();
//        paasApplicationService.createService(appGuid, softwareInstance.getId(), paasSoftware.getPlanGuid());
//
//        softwareInstance.setUrl(name + platformPaasHostName);
//        softwareInstance.setAppGuid(appGuid);
//        softwareInstance.setName(name);
//        softwareInstance.setKeyGuid(name);
//
//        getAppStats(appGuid, name);
//
//    }
//
//
//    public Catalog parseCatalog(UserProduct){
//
//    }


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

//    @Async("provisionExecutor")
//    public Future<UserProduct> provision(Long id) {
//        UserProduct userProduct = userProductRepository.findById(id).get();
//
//        try {
//            log.info("provision success: {}", userProduct.getId());
//
//            // todo :: deprovision
//            //deprovision(userProduct);
//            //provisionUserProduct(userProduct);
//
//            userProduct.setProvisionStatus(ApiConstants.PROVISION_STATUS_SUCCESS);
//            userProduct.setUseEnddate(LocalDateTime.now());
//
//        } catch (Exception e) {
//            log.info("provision failed: {}", userProduct.getId());
//
//            userProduct.setProvisionTryCount(userProduct.getProvisionTryCount() + 1);
//            if (userProduct.getProvisionTryCount() >= provisioningTryCount) {
//                userProduct.setProvisionStatus(ApiConstants.PROVISION_STATUS_FAIL);
//            } else {
//                userProduct.setProvisionStatus(ApiConstants.PROVISION_STATUS_INPROGRESS);
//            }
//            userProduct.setUseEnddate(LocalDateTime.now());
//        }
//
//        return new AsyncResult<>(userProduct);
//    }

}
