package org.openpaas.paasta.marketplace.api.domain;

import java.io.File;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public abstract class Software extends CommonEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Category category;

    @OneToMany
    private List<Screenshot> screenshotList;

    @OneToMany(mappedBy = "software")
    @JsonIgnore
    private List<SoftwareInstance> softwareInstanceList;

    private String name;

    private String description;

    private String detailDescription;

    @Transient
    private File appIcon;

    // 아이콘 파일 이름
    private String iconFileName;

    // 아이콘 파일 경로
    private String iconFilePath;

    @Enumerated(EnumType.STRING)
    protected Type type;

    @Enumerated(EnumType.STRING)
    protected Status status;

    @Enumerated(EnumType.STRING)
    protected InnerUserApprovalYn innerUserApprovalYn;

    @Enumerated(EnumType.STRING)
    protected DisplayYn displayYn;

    public enum Type {
        IaaS, PaaS, Install,
    };

    public enum Status {
        Pending, Approval, Rejected,
    };

    public enum InnerUserApprovalYn{
        Y, N,
    }

    public enum DisplayYn{
        Y, N,
    }

}
