package org.openpaas.paasta.marketplace.api.util;

import org.openpaas.paasta.marketplace.api.common.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

	private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    public static Users getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("authentication={}", authentication);

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        logger.debug("principal={}", principal);

        if (principal == null) {
            return null;
        }

        if (principal instanceof Users) {
            return (Users) principal;
        }

        return null;
    }

    public static String getUserId() {
        Users user = getUser();

        if (user == null) {
            return null;
        }

        return user.getUsername();
    }

}
