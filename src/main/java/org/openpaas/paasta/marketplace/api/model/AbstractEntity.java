package org.openpaas.paasta.marketplace.api.model;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.openpaas.paasta.marketplace.api.util.SecurityUtils;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbstractEntity {

	@Enumerated(EnumType.STRING)
    protected UseYn useYn;

    protected String createdId;

    protected String updatedId;

    protected Date createdDate;

    protected Date updatedDate;

    @PrePersist
    public void prePersist() {
        useYn = UseYn.Y;
        createdId = SecurityUtils.getUserId();
        createdDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updatedId = SecurityUtils.getUserId();
        updatedDate = new Date();
    }

    public enum UseYn {
        Y, N, All,
    };

}
