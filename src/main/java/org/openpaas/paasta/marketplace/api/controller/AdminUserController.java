package org.openpaas.paasta.marketplace.api.controller;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.User;
import org.openpaas.paasta.marketplace.api.domain.UserSpecification;
import org.openpaas.paasta.marketplace.api.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-09-01
 */
@RestController
@RequestMapping(value = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping("/page")
    public Page<User> getPage(UserSpecification spec, Pageable pageable) {
        return userService.getUserList(spec, pageable);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUser(id);
    }
}
