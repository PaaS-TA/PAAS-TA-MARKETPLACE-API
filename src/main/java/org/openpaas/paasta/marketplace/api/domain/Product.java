package org.openpaas.paasta.marketplace.api.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper=false)
public class Product extends BaseEntity {

	// 상품ID
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Category category;

    @ManyToOne
    private SellerProfile seller;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Screenshot> screenshots;
    
    @Transient
    private List<MultipartFile> screenshotFiles;

    // 상품명
    @NotNull
    private String productName;
    
    // 버전정보
    @NotNull
    private String versionInfo;

    // 상품개요
    @NotNull
    private String simpleDescription;

    // 상품상세
    private String detailDescription;

    // 상품유형
    @NotNull
    @Enumerated(EnumType.STRING)
    private SwType productType;

    // 파일경로
    @NotNull
    private String filePath;
    
//    @Transient: 데이터를 디스크에 저장하거나 디비에 저장하거나 http request를 통해 보내거나 할때 민감한 데이터(개인정보등)은 제외하고 싶을 때 사용
//    private File appIcon;

    // 아이콘 파일 이름
    @NotNull
    private String iconFileName;
    
    @Transient
    private MultipartFile iconFile;

    // 상품 파일 이름
    @NotNull
    private String productFileName;
    
    // 환경 파일 이름
    @NotNull
    private String envFileName;

    // 미터링 유형
    @NotNull
    private String meteringType;
    
    // 미터링 금액
    @NotNull
    private int unitPrice;

    // 전시여부
    @NotNull
//    @Enumerated(EnumType.STRING)
//    protected DisplayYn displayYn;
    private String displayYn;

    // 승인상태
    @NotNull
//    @Enumerated(EnumType.STRING)
//    protected Status approvalStatus;
    private String approvalStatus;

    // 반려 사유
    private String rejectReason;
    
    // 승인 일자
    private LocalDateTime approvalDate;

    @NotNull
    private String deleteYn;

    @PrePersist
    public void prePersist() {
    	deleteYn = "N";
    	meteringType = "DAY";
    }

    public enum SwType {
        WEB, API,
    };

//    public enum Status {
//        READY, APPROVED, REJECTED,
//    };

//    public enum DisplayYn{
//        Y, N, ALL,
//    }

}
