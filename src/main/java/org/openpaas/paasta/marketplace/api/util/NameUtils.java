package org.openpaas.paasta.marketplace.api.util;

import java.util.UUID;

public abstract class NameUtils {

    public static String makeUniqueName() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", ""); // -를 제거
        uuid = uuid.substring(0, 10);

        return uuid;
    }
}
