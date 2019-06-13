package org.openpaas.paasta.marketplace.api.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 판매자 프로필 모델
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */
@Getter
@Setter
@ToString(exclude="products")
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SellerProfile extends BaseEntity{

	// ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_profile_id", referencedColumnName = "id")
    @JsonIgnore
    private List<Product> products;

    // 판매자 로그인ID
    @Column(name = "seller_id", unique = true)
    @NotNull
    private String sellerId;
    
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
