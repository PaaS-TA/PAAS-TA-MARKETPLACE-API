package org.openpaas.paasta.marketplace.api.controller;

import javax.validation.constraints.NotNull;

import org.openpaas.paasta.marketplace.api.domain.Profile;
import org.openpaas.paasta.marketplace.api.domain.ProfileSpecification;
import org.openpaas.paasta.marketplace.api.service.ProfileService;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/admin/profiles")
@RequiredArgsConstructor
public class AdminSellerProfileController {

    private final ProfileService profileService;

    @GetMapping("/page")
    public Page<Profile> getPage(ProfileSpecification spec, Pageable pageable) {
        return profileService.getPage(spec, pageable);
    }

    @GetMapping("/{id}")
    public Profile get(@NotNull @PathVariable String id) {
        return profileService.get(id);
    }
    
 
    @PutMapping("/{id}")
    public Profile update(@PathVariable @NotNull String id, @NotNull @Validated @RequestBody Profile profile) {
        return profileService.update(profile);
    }
}






