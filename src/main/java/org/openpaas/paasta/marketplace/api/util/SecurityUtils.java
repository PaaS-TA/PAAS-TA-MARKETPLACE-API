package org.openpaas.paasta.marketplace.api.util;

import org.openpaas.paasta.marketplace.api.domain.AbstractEntity;
import org.openpaas.paasta.marketplace.api.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SecurityUtils {

    public static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("authentication={}", authentication);

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        log.debug("principal={}", principal);

        if (principal == null) {
            return null;
        }

        if (principal instanceof User) {
            return (User) principal;
        }

        return null;
    }

    public static String getUserId() {
        User user = getUser();

        if (user == null) {
            return null;
        }

        return user.getId();
    }

    public static void assertCreator(AbstractEntity entity) {
        Assert.notNull(entity, "Entity can't be null.");
        Assert.notNull(entity.getCreatedBy(), "Entity's createdId can't be null.");
        Assert.isTrue(entity.getCreatedBy().equals(getUserId()),
                "Entity's createdBy must be equals to user's id. Entity's createdBy is '" + entity.getCreatedBy()
                        + "'. But user's id is '" + getUserId() + "'");
    }

    public static void assertUser(String userId) {
        Assert.notNull(userId, "User's id can't be null.");
        Assert.isTrue(userId.equals(getUserId()), "userId must be equals to user's id. userId is '" + userId
                + "'. But user's id is '" + getUserId() + "'");
    }

    public static boolean isCreator(AbstractEntity entity) {
        String createdId = entity.getCreatedBy();
        if (createdId == null) {
            return false;
        }

        return createdId.equals(getUserId());
    }

}
