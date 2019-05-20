package org.openpaas.paasta.marketplace.api.domain;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import org.openpaas.paasta.marketplace.api.util.SecurityUtils;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class BaseEntity {

	@NotNull
	protected String createId;

	@NotNull
    protected String updateId;

	@NotNull
//    @Column(nullable = false, updatable = false)
    private Date createDate;

	@NotNull
//    @Column(nullable = false)
    private Date updateDate;

    @PrePersist
    public void prePersist() {
        createId = SecurityUtils.getUserId();
//        if (this.createDate == null) {
//            this.createDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
//        }

//        if (this.updateDate == null) {
//            this.updateDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
//        }
        createDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updateId = SecurityUtils.getUserId();
//        if (this.updateDate != null) {
//            this.updatedDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
//        }
        updateDate = new Date();
    }

}
