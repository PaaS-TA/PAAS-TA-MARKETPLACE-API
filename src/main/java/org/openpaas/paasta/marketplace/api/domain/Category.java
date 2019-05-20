package org.openpaas.paasta.marketplace.api.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

	// 카테고리ID
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	// 카테고리명
    @NotNull
    private String categoryName;

    @NotNull
    private String deleteYn;

//    @OneToMany(mappedBy = "category")
//    @JsonIgnore
//    private List<Product> productList;
//
//    @PreRemove
//    private void preRemove() {
//        for (Product product : productList) {
//        	product.setCategory(null);
//        }
//    }

    @PrePersist
    public void prePersist() {
    	deleteYn = "N";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Category [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(categoryName);
        builder.append("]");

        return builder.toString();
    }

}
