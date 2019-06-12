package org.openpaas.paasta.marketplace.api.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

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
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Transient
    private Long categoryId;

    @ManyToOne
    @JoinColumn(name = "seller_profile_id")
    private SellerProfile seller;
    
    @Transient
    private String sellerId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private List<Screenshot> screenshots;
    
    @Transient
    private List<String> screenshotFileNames;

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
    
    // 아이콘 파일 이름
    @NotNull
    private String iconFileName;
    
//    @Transient
//    private MultipartFile iconFile;

    // 상품 파일 이름
    @NotNull
    private String productFileName;
    
//    @Transient
//    private MultipartFile productFile;
    
    // 환경 파일 이름
    @NotNull
    private String envFileName;
    
//    @Transient
//    private MultipartFile envFile;

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
    	approvalStatus = "READY";
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
