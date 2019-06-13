package org.openpaas.paasta.marketplace.api.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * Common Service
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-06-04
 */
@Service
@Slf4j
public class CommonService {

	private final Gson gson = new Gson();

	/**
     * Sets result model.
     *
     * @param reqObject  the req object
     * @param resultCode the result code
     * @return the result model
     */
    public Object setResultModel(Object reqObject, String resultCode) {
        try {
            Class<?> aClass = reqObject.getClass();

            Method methodSetResultCode = aClass.getMethod("setResultCode", String.class);
            methodSetResultCode.invoke(reqObject, resultCode);

        } catch (NoSuchMethodException e) {
            log.error("NoSuchMethodException :: {}", e);
        } catch (IllegalAccessException e1) {
            log.error("IllegalAccessException :: {}", e1);
        } catch (InvocationTargetException e2) {
            log.error("InvocationTargetException :: {}", e2);
        }

        return reqObject;
    }


    /**
     * Sets result object.
     *
     * @param <T>           the type parameter
     * @param requestObject the request object
     * @param requestClass  the request class
     * @return the result object
     */
    public <T> T setResultObject(Object requestObject, Class<T> requestClass) {
        return this.fromJson(this.toJson(requestObject), requestClass);
    }


    /**
     * To json string.
     *
     * @param requestObject the request object
     * @return the string
     */
    private String toJson(Object requestObject) {
        return gson.toJson(requestObject);
    }


    /**
     * From json t.
     *
     * @param <T>           the type parameter
     * @param requestString the request string
     * @param requestClass  the request class
     * @return the t
     */
    private <T> T fromJson(String requestString, Class<T> requestClass) {
        return gson.fromJson(requestString, requestClass);
    }

    /**
     * Sets page info.
     *
     * @param reqPage   the req page
     * @param reqObject the req object
     * @return the page info
     */
    public Object setPageInfo(Page reqPage, Object reqObject) {
        Object resultObject = null;

        try {
            Class<?> aClass;

            if (reqObject instanceof Class) {
                aClass = (Class<?>) reqObject;
                resultObject = ((Class) reqObject).newInstance();
            } else {
                aClass = reqObject.getClass();
                resultObject = reqObject;
            }

            Method methodSetPage = aClass.getMethod("setPage", Integer.TYPE);
            Method methodSetSize = aClass.getMethod("setSize", Integer.TYPE);
            Method methodSetTotalPages = aClass.getMethod("setTotalPages", Integer.TYPE);
            Method methodSetTotalElements = aClass.getMethod("setTotalElements", Long.TYPE);
            Method methodSetLast = aClass.getMethod("setLast", Boolean.TYPE);

            methodSetPage.invoke(resultObject, reqPage.getNumber());
            methodSetSize.invoke(resultObject, reqPage.getSize());
            methodSetTotalPages.invoke(resultObject, reqPage.getTotalPages());
            methodSetTotalElements.invoke(resultObject, reqPage.getTotalElements());
            methodSetLast.invoke(resultObject, reqPage.isLast());

        } catch (NoSuchMethodException e) {
            log.error("NoSuchMethodException :: {}", e);
        } catch (IllegalAccessException e1) {
            log.error("IllegalAccessException :: {}", e1);
        } catch (InvocationTargetException e2) {
            log.error("InvocationTargetException :: {}", e2);
        } catch (InstantiationException e3) {
            log.error("InstantiationException :: {}", e3);
        }

        return resultObject;
    }

}
