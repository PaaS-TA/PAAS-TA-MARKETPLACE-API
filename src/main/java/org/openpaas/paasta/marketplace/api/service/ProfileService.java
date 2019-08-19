package org.openpaas.paasta.marketplace.api.service;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Profile;
import org.openpaas.paasta.marketplace.api.domain.ProfileSpecification;
import org.openpaas.paasta.marketplace.api.repository.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Profile create(Profile profile) {
        return profileRepository.save(profile);
    }

    public Page<Profile> getPage(ProfileSpecification spec, Pageable pageable) {
        return profileRepository.findAll(spec, pageable);
    }

    public Profile get(String id) {
        return profileRepository.findById(id).orElse(null);
    }

    public Profile update(Profile profile) {
        Profile saved = profileRepository.findById(profile.getId()).get();
        saved.setName(profile.getName());
        saved.setType(profile.getType());
        saved.setManager(profile.getManager());
        saved.setEmail(profile.getEmail());
        saved.setSiteUrl(profile.getSiteUrl());

        return saved;
    }

}
