package org.openpaas.paasta.marketplace.api.domain;

import lombok.Data;
import org.openpaas.paasta.marketplace.api.util.Constants;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
@Data
public abstract class CommonEntity {

    protected String createdId;

    protected String updatedId;

    @Column(name = "created_date", nullable = false, updatable = false)
    private String createdDate;

    @Column(name = "updated_date", nullable = false)
    private String updatedDate;

    @PrePersist
    public void prePersist() {
        createdId = SecurityUtils.getUserId();
        if (this.createdDate == null) {
            this.createdDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }

        if (this.updatedDate == null) {
            this.updatedDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedId = SecurityUtils.getUserId();
        if (this.updatedDate != null) {
            this.updatedDate = LocalDateTime.now(ZoneId.of(Constants.STRING_TIME_ZONE_ID)).format(DateTimeFormatter.ofPattern(Constants.STRING_DATE_TYPE));
        }
    }

}
