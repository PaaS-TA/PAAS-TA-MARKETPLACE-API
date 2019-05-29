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

    public static final String STRING_TIME_ZONE_ID = "Asia/Seoul";
    public static final String STRING_DATE_TYPE = "yyyy-MM-dd HH:mm:ss";

    public static final String DELETE_YN_Y = "Y";
    public static final String DELETE_YN_N = "N";

    // api common uri
    public static final String URI_API_CUSTOM_CODE = "/api/customCode";
    public static final String URI_API_ADMIN = "/api/admin";
    public static final String URI_API_SELLER = "/api/seller";
    public static final String URI_API_USER = "/api/user";
    
    // api web uri
    public static final String URI_API_CATEGORY = "/api/category";
    public static final String URI_API_SELLER_PROFILE = "/api/seller/profile";
    
    private ApiConstants() {
        throw new IllegalStateException();
    }
}
