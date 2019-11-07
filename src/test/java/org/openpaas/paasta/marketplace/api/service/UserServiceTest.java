package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.User;
import org.openpaas.paasta.marketplace.api.domain.UserSpecification;
import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class UserServiceTest extends AbstractMockTest {

    UserService userService;

    @Mock
    UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        userService = new UserService(userRepository);
    }

    @Test
    public void getUserList() {
        UserSpecification spec = new UserSpecification();
        Pageable pageRequest = PageRequest.of(0, 10);

        User user1 = user("user-01");
        User user2 = user("user-02");

        List<User> userList = new ArrayList<User>();
        userList.add(user1);
        userList.add(user2);

        Page<User> page = new PageImpl<>(userList);

        given(userRepository.findAll(any(UserSpecification.class), any(Pageable.class))).willReturn(page);

        Page<User> result = userService.getUserList(spec, pageRequest);
        assertEquals(page, result);

        verify(userRepository).findAll(any(UserSpecification.class), any(Pageable.class));
    }

    @Test
    public void get() {
        User user1 = user("user-01");

        given(userRepository.findById(any(String.class))).willReturn(Optional.of(user1));

        User result = userService.getUser("user-01");
        assertEquals(user1, result);

        verify(userRepository).findById("user-01");
    }

}
