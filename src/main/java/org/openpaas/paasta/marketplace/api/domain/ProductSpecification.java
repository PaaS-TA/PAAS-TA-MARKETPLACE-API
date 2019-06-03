package org.openpaas.paasta.marketplace.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lombok.Data;

/**
 * 상품 Specification
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-06-03
 */
@Data
public class ProductSpecification implements Specification<Product> {

	private static final long serialVersionUID = 1L;

	private Long categoryId;
	
	private Product.Type type;
	
	private String createId;

	private Product.DisplayYn displayYn;

	private Product.Status approvalStatus;

	private String productName;

	private String sellerName;
	
	@Override
	public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		List<Predicate> restrictions = new ArrayList<>();

		if (categoryId != null) {
			restrictions.add(builder.equal(root.get("category").get("id"), categoryId));
		}
		if (type != null) {
			restrictions.add(builder.equal(root.get("type"), type));
		}
		if (createId != null) {
			restrictions.add(builder.equal(root.get("createdId"), createId));
		}
		if (productName != null) {
			restrictions.add(builder.like(root.get("productName"), "%" + productName + "%"));
		}
		if (sellerName != null) {
			restrictions.add(builder.like(root.get("sellerName"), "%" + sellerName + "%"));
		}
		if (displayYn != null && !displayYn.equals(Product.DisplayYn.ALL)) {
			restrictions.add(builder.equal(root.get("displayYn"), displayYn));
		}
		if (approvalStatus != null) {
			restrictions.add(builder.equal(root.get("approvalStatus"), approvalStatus));
		}
	
		query.orderBy(builder.asc(root.get("productName")));
	
		return builder.and(restrictions.toArray(new Predicate[] {}));
	}

}
