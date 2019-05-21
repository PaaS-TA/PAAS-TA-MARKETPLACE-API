package org.openpaas.paasta.marketplace.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Custom Code 모델
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-08
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class CustomCode extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ex) BUSINESS_TYPE
    @NotNull
    private String groupCode;

    // ex) 업체유형
    @NotNull
    private String groupCodeName;

    // ex) GOVERNMENT, COMPANY, PERSON, ETC
    @NotNull
    private String unitCode;

    // ex) 공공기관, 기업, 개인, 기타
    @NotNull
    private String unitCodeName;

}
