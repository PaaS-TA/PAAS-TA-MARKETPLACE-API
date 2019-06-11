package org.openpaas.paasta.marketplace.api.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
	
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="userId")
    private List<Product> products;

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

    @PrePersist
    public void prePersist() {
    	provisionStatus = "READY";
    }

}
