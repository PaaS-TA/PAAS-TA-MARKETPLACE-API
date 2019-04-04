package org.openpaas.paasta.marketplace.api.thirdparty.paas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Entity
@Data
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Transient
    private String token;

    private Date createdDate;

    private Date updatedDate;

    public enum Role implements GrantedAuthority {
        // 관리자
        Admin,
        // 연구원
        Manager,
        // 사용자
        User,;

        @Override
        public String getAuthority() {
            return name();
        }
    }

    public User() {
    }

    @PrePersist
    public void prePersist() {
        createdDate = new Date();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<Role> roles = new ArrayList<>();
        roles.add(role);

        return roles;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
