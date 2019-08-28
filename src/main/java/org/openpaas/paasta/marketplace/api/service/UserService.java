package org.openpaas.paasta.marketplace.api.service;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.User;
import org.openpaas.paasta.marketplace.api.domain.UserSpecification;
import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service("marketUserService")
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<User> getUserList(UserSpecification spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    public User getUser(String id) {
        return userRepository.findById(id).get();
    }

}
