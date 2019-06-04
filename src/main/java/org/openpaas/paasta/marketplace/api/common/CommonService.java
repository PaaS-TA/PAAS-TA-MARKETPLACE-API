package org.openpaas.paasta.marketplace.api.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Common Service
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-06-04
 */
@Slf4j
@Service
public class CommonService {

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
