package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProfileRepository extends JpaRepository<Profile, String>, JpaSpecificationExecutor<Profile> {

}
