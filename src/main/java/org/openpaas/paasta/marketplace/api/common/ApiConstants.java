package org.openpaas.paasta.marketplace.api.common;

/**
 * Constants 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-13
 */
public class ApiConstants {

	// common
	public static final String RESULT_STATUS_SUCCESS = "SUCCESS";
    public static final String RESULT_STATUS_FAIL = "FAIL";

    public static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    public static final String CF_AUTHORIZATION_HEADER_KEY = "cf-Authorization";
    public static final String CONTENT_TYPE = "Content-Type";

    public static final String TARGET_API_CF = "cfApi";

    public static final String STRING_TIME_ZONE_ID = "Asia/Seoul";
    public static final String STRING_DATE_TYPE = "yyyy-MM-dd HH:mm:ss";
    
    public static final int MAX_FILE_SIZE = 5;

    // code
    public static final String DELETE_YN_Y = "Y";
    public static final String DELETE_YN_N = "N";
    public static final String DISPLAY_YN_Y = "Y";
    public static final String DISPLAY_YN_N = "N";
    public static final String BUSINESS_TYPE_GOVERNMENT = "GOVERNMENT";
    public static final String BUSINESS_TYPE_COMPANY = "COMPANY";
    public static final String BUSINESS_TYPE_PERSON = "PERSON";
    public static final String BUSINESS_TYPE_ETC = "ETC";
    public static final String METERING_TYPE_DAY = "DAY";
    public static final String APPROVAL_STATUS_READY = "READY";
    public static final String APPROVAL_STATUS_APPROVED = "APPROVED";
    public static final String APPROVAL_STATUS_REJECTED = "REJECTED";
    public static final String PROVISION_STATUS_READY = "READY";
    public static final String PROVISION_STATUS_INPROGRESS = "INPROGRESS";
    public static final String PROVISION_STATUS_SUCCESS = "SUCCESS";
    public static final String PROVISION_STATUS_FAIL = "FAIL";
    public static final String PROVISION_STATUS_STOP = "STOP";

    // api common uri
    public static final String URI_API_CUSTOM_CODE = "/api/customCode";
    public static final String URI_API_ADMIN = "/api/admin";
    public static final String URI_API_SELLER = "/api/seller";
    public static final String URI_API_USER = "/api/user";
    
    // api web uri
    public static final String URI_API_CATEGORY = "/api/category";
    public static final String URI_API_SELLER_PROFILE = "/api/seller/profile";
    public static final String URI_API_PRODUCT = "/api/seller/product";
    public static final String URI_API_USER_PRODUCT = "/api/user/product";
    
    private ApiConstants() {
        throw new IllegalStateException();
    }
}
