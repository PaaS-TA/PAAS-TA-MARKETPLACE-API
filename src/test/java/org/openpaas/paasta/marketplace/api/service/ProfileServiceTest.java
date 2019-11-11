package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.Profile;
import org.openpaas.paasta.marketplace.api.domain.ProfileSpecification;
import org.openpaas.paasta.marketplace.api.repository.ProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ProfileServiceTest extends AbstractMockTest {

    ProfileService profileService;

    @Mock
    ProfileRepository profileRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        profileService = new ProfileService(profileRepository);
    }

    @Test
    public void create() {
        Profile profile1 = profile("user-01");

        given(profileRepository.save(any(Profile.class))).willReturn(profile1);

        Profile result = profileService.create(profile1);
        assertEquals(profile1, result);

        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    public void getPage() {
        ProfileSpecification spec = new ProfileSpecification();
        Pageable pageRequest = PageRequest.of(0, 10);

        Profile profile1 = profile("user-01");
        Profile profile2 = profile("user-02");

        List<Profile> profileList = new ArrayList<Profile>();
        profileList.add(profile1);
        profileList.add(profile2);

        Page<Profile> page = new PageImpl<>(profileList);

        given(profileRepository.findAll(any(ProfileSpecification.class), any(Pageable.class))).willReturn(page);

        Page<Profile> result = profileService.getPage(spec, pageRequest);
        assertEquals(page, result);

        verify(profileRepository).findAll(any(ProfileSpecification.class), any(Pageable.class));
    }

    @Test
    public void get() {
        Profile profile1 = profile("user-01");

        given(profileRepository.findById(any(String.class))).willReturn(Optional.of(profile1));

        Profile result = profileService.get("user-01");
        assertEquals(profile1, result);

        verify(profileRepository).findById(any(String.class));
    }

    @Test
    public void update() {
        Profile profile1 = profile("user-01");
        Profile profile1_1 = profile("user-01");
        profile1_1.setStatus(Profile.Status.Rejected);

        given(profileRepository.findById(any(String.class))).willReturn(Optional.of(profile1));

        Profile result = null;
        result = profileService.update(profile1);
        assertEquals(profile1, result);

        result = profileService.update(profile1_1);
        assertEquals(profile1, result);

        verify(profileRepository, atLeastOnce()).findById(any(String.class));
    }

}
