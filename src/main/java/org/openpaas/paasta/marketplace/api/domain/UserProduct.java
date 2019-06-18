package org.openpaas.paasta.marketplace.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class UserProduct extends BaseEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	// 사용자ID
	@NotNull
	private String userId;
	
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_product_id")
//    private List<Product> products;

    @Transient
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // 사용자명
    @NotNull
    private String userName;
    
    // 상품명
    @NotNull
    private String productName;
    
    // 미터링 유형
    @NotNull
    private String meteringType;

    // 요금
    @NotNull
    private int unitPrice;

    // 구매상태
    @NotNull
    private String provisionStatus;
    
    // 사용시작일자
    @CreationTimestamp
    private LocalDateTime useStartdate;
    
    // 사용종료일자
    private LocalDateTime useEnddate;
    
    // 접속URL
    private String accessUrl;

    private Integer provisionTryCount = 0;

    private Integer deprovisionTryCount = 0;

    private String uuid;

    @PrePersist
    public void prePersist() {
    	provisionStatus = "READY";
    }

}
