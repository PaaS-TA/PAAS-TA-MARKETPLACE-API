package org.openpaas.paasta.marketplace.api.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Custom Code 모델
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-08
 */
@Data
@Entity
public class CustomCode extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // ex) BUSINESS_CODE
    @NotNull
    private String groupCode;

    // ex) 비즈니스 타입 코드
    @NotNull
    private String groupCodeName;

    // ex) GOVERNMENT, COMPANY, PERSON, ETC
    @NotNull
    private String codeUnit;

    // ex) 공공기관, 기업, 개인, 기타
    @NotNull
    private String codeUnitName;
}
