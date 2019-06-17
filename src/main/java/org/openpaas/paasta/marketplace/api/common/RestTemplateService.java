package org.openpaas.paasta.marketplace.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Rest Template Service 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2019.03.25
 */
@Slf4j
@Service
public class RestTemplateService {

    @Autowired
    private PropertyService property;

    @Autowired
    private RestTemplate restTemplate;

    public <T> T send(String targetApi, String restUrl, String token, HttpMethod httpMethod, Object bodyObject, Class<T> responseType) {
        Map<String, String> requestMap = setApiUrlAuthorization(targetApi, token);
        String apiFullUrl = requestMap.get("apiUrl") + restUrl;

        HttpHeaders reqHeaders = new HttpHeaders();
        if(ApiConstants.TARGET_API_CF.equals(targetApi)) {
            reqHeaders.add(ApiConstants.CF_AUTHORIZATION_HEADER_KEY, requestMap.get("authorizationCf"));
            reqHeaders.add(ApiConstants.AUTHORIZATION_HEADER_KEY, requestMap.get("authorizationBasic"));
            reqHeaders.add(ApiConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            reqHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        }


        HttpEntity<Object> reqEntity = new HttpEntity<>(bodyObject, reqHeaders);
        log.info("<T> T send :: Request : {} {} : {}, Content-Type: {}", httpMethod, apiFullUrl, restUrl, reqHeaders.get(ApiConstants.CONTENT_TYPE));

        try {
            ResponseEntity<T> resEntity =  restTemplate.exchange(apiFullUrl, httpMethod, reqEntity, responseType);
            if (resEntity.getBody() != null) {
                log.info("Response Type: {}", resEntity.getBody().getClass());
                log.info(resEntity.getBody().toString());
            } else {
                log.info("Response Type: {}", "response body is null");
            }

            return resEntity.getBody();
        } catch (Exception e) {
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("resultCode", "FAIL");
            resultMap.put("resultMessage", e.getMessage());
            ObjectMapper mapper = new ObjectMapper();

            log.error("Error resultMap : {}", resultMap);

            return mapper.convertValue(resultMap, responseType);
        }
    }

    /**
     * 전송용 헤더 생성
     *
     * @param targetApi
     * @return
     */
    private Map<String, String> setApiUrlAuthorization(String targetApi, String token) {
        Map<String, String> requestMap = new HashMap<String, String>();

        // Cf API
        if (ApiConstants.TARGET_API_CF.equals(targetApi)) {
            requestMap.put("apiUrl", property.getCfJavaClientApiUri());
            requestMap.put("authorizationCf", "bearer " + token);
            requestMap.put("authorizationBasic", "Basic " + Base64Utils.encodeToString((property.getCfJavaClientApiUsername() + ":" + property.getCfJavaClientApiPassword()).getBytes(StandardCharsets.UTF_8)));
        }

        return requestMap;
    }

}
