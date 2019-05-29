package org.openpaas.paasta.marketplace.api.domain;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class BaseEntity {

	@NotNull
	protected String createId;

	@NotNull
    protected String updateId;

//	@NotNull
	@CreationTimestamp
	protected LocalDateTime createDate;

//	@NotNull
	@UpdateTimestamp
	protected LocalDateTime updateDate;

	@Transient
	private String resultCode;

	@Transient
	private String resultMessage;

    @PrePersist
    public void preInsert() {
//        if (this.createDate == null) {
//            this.createDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
//        }

//        if (this.updateDate == null) {
//            this.updateDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
//        }
        createDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
//        if (this.updateDate != null) {
//            this.updatedDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
//        }
        updateDate = LocalDateTime.now();
    }

}
