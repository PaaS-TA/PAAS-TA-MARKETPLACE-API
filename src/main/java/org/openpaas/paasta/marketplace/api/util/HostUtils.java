package org.openpaas.paasta.marketplace.api.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class HostUtils {

    public static String getHostName() {
        String hostName = "unknown";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn(e.getMessage(), e);
        }

        return hostName;
    }

}
