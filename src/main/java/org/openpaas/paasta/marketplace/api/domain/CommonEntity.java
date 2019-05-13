package org.openpaas.paasta.marketplace.api.domain;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.openpaas.paasta.marketplace.api.util.SecurityUtils;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class CommonEntity {

    protected String createdId;

    protected String updatedId;

    protected Date createdDate;

    protected Date updatedDate;

    @PrePersist
    public void prePersist() {
        createdId = SecurityUtils.getUserId();
        createdDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updatedId = SecurityUtils.getUserId();
        updatedDate = new Date();
    }
    
}
