package org.openpaas.paasta.marketplace.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Screenshot extends BaseEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@NotNull
	private String screenshotFilePath;
	
	@NotNull
    private String screenshotFileName;
    
    @NotNull
    private String deleteYn;

    @ManyToOne
    @JoinColumn(name="productId")
    private Product product;

    @PrePersist
    public void prePersist() {
    	deleteYn = "N";
    }
    
}
