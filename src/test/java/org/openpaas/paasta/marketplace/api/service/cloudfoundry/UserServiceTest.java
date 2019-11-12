package org.openpaas.paasta.marketplace.api.service.cloudfoundry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.users.ListUsersResponse;
import org.cloudfoundry.uaa.users.Meta;
import org.cloudfoundry.uaa.users.Name;
import org.cloudfoundry.uaa.users.User;
import org.cloudfoundry.uaa.users.Users;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserService userService;

    @Mock
    ReactorUaaClient uaaClient;

    @Mock
    Users users;

    @Mock
    Mono<ListUsersResponse> listUsersResponseMono;

    boolean notExistUser;

    @Before
    public void setUp() throws Exception {
        notExistUser = false;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void constructor() {
        userService = new UserService();
    }

    @Test
    public void getUserIdByUsername() {
        given(userService.uaaClient()).willReturn(uaaClient);
        given(uaaClient.users()).willReturn(users);
        given(users.list(any())).willReturn(listUsersResponseMono);
        ListUsersResponse listUsersResponse = ListUsersResponse.builder().itemsPerPage(10).startIndex(0).totalResults(0)
                .build();
        if (!notExistUser) {
            Meta meta = Meta.builder().created("x").lastModified("x").version(1).build();
            Name name = Name.builder().build();
            User user = User.builder().id("1234").userName("x").active(true).meta(meta).name(name)
                    .passwordLastModified("x").verified(true).zoneId("x").origin("x").build();
            listUsersResponse = ListUsersResponse.builder().resource(user).itemsPerPage(10).startIndex(0)
                    .totalResults(1).build();
        }
        given(listUsersResponseMono.block()).willReturn(listUsersResponse);

        given(userService.getUserIdByUsername(any())).willCallRealMethod();

        String result = userService.getUserIdByUsername("x");
        if (!notExistUser) {
            assertEquals("1234", result);
        } else {
            assertNull(result);
        }
    }

    @Test
    public void getUserIdByUsernameNull() {
        notExistUser = true;

        getUserIdByUsername();
    }

}
