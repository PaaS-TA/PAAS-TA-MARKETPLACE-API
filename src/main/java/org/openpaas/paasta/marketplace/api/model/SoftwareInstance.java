package org.openpaas.paasta.marketplace.api.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.openpaas.paasta.marketplace.api.model.Software.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SoftwareInstance extends AbstractEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Software software;

    private String name;

    @Enumerated(EnumType.STRING)
    protected Type type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private ProvisionStatus provisionStatus;

    public enum Status {
        Pending, Approval, Rejected,
    }

    public enum ProvisionStatus {
        Pending, InProgress, Successful, Failed,
    }

}
