package org.openpaas.paasta.marketplace.api.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public abstract class SoftwareInstance extends BaseEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    private String name;

//    @Enumerated(EnumType.STRING)
//    protected Type type;

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
