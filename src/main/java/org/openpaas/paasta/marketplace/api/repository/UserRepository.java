package org.openpaas.paasta.marketplace.api.repository;

import org.openpaas.paasta.marketplace.api.thirdparty.paas.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

}
