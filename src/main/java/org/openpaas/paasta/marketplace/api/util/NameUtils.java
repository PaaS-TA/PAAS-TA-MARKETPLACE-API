package org.openpaas.paasta.marketplace.api.util;

import java.util.List;

public abstract class NameUtils {

    public static final int DEFAULT_MAX_NUMBER = 10000 - 1;

    public static String makeUniqueName(String originName, List<String> existingNames) {
        return makeUniqueName(originName, existingNames, DEFAULT_MAX_NUMBER);
    }

    public static String makeUniqueName(String originName, List<String> existingNames, int maxNumber) {
        String candidate = originName;
        for (int i = 1; i <= maxNumber + 1; i++) {
            if (!existingNames.contains(candidate)) {
                return candidate;
            }

            candidate = originName + "(" + i + ")";
        }

        throw new RuntimeException("can't make a unique name. originName=" + originName);
    }

}
