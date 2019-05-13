package org.openpaas.paasta.marketplace.api.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

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

}
