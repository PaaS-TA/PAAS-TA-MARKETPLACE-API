package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import org.cloudfoundry.uaa.users.ListUsersRequest;
import org.cloudfoundry.uaa.users.User;
import org.openpaas.paasta.marketplace.api.config.common.Common;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-08-19
 */
@Service
public class UserService extends Common {
    private enum UaaUserLookupFilterType {Username, Id, Origin}

    private String createUserLookupFilter(UaaUserLookupFilterType filterType, String filterValue) {
        Objects.requireNonNull(filterType, "User lookup FilterType");
        Objects.requireNonNull(filterValue, "User lookup FilterValue");

        StringBuilder builder = new StringBuilder();
        builder.append(filterType.name()).append(" eq \"").append(filterValue).append("\"");
        return builder.toString();
    }

    /**
     * 유저 이름(user name)으로 유저의 GUID(user id)를 가져온다.
     *
     * @param username
     * @return User ID
     */
    public String getUserIdByUsername(String username) {
        final List<User> userList = uaaClient().users().list(ListUsersRequest.builder().filter(createUserLookupFilter(UaaUserLookupFilterType.Username, username)).build()).block().getResources();
        if (userList.size() <= 0) {
            //throw new CloudFoundryException( HttpStatus.NOT_FOUND, "User name cannot find" );
            return null;
        }
        return userList.get(0).getId();
    }
}
