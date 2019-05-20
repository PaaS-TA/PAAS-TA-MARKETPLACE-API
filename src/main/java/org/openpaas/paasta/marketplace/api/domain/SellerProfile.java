package org.openpaas.paasta.marketplace.api.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 판매자 프로필 모델
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SellerProfile extends BaseEntity{

	// 판매자ID
    @Id
    private String id;

    // 판매자명
    @NotNull
    private String sellerName;

    // 업체유형
    @NotNull
    private String businessType;

    @Transient
    private List<CustomCode> businessTypeList;

    // 관리자명
    @NotNull
    private String managerName;

    // 이메일주소
    @NotNull
    private String email;

    // 홈페이지주소
    private String homepageUrl;
    
    @NotNull
    private String deleteYn;
    
    @PrePersist
    public void prePersist() {
    	deleteYn = "N";
    }

}
