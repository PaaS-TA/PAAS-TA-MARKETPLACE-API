package org.openpaas.paasta.marketplace.api.model;

import lombok.Data;
import org.openpaas.paasta.marketplace.api.common.Constants;
import org.openpaas.paasta.marketplace.api.util.SecurityUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
@Data
public abstract class AbstractEntity {

	@Enumerated(EnumType.STRING)
    protected UseYn useYn;

    protected String createdId;

    protected String updatedId;

    @Column(name = "created_date", nullable = false, updatable = false)
    private String createdDate;

    @Column(name = "updated_date", nullable = false)
    private String updatedDate;

    @PrePersist
    public void prePersist() {
        useYn = UseYn.Y;
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

    public enum UseYn {
        Y, N, All,
    };

}
