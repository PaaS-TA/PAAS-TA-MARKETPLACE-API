package org.openpaas.paasta.marketplace.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import lombok.Data;

@Data
public class SellerProfileSpecification implements Specification<SellerProfile> {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate toPredicate(Root<SellerProfile> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		List<Predicate> restrictions = new ArrayList<>();

        restrictions.add(builder.equal(root.get("deleteYn"), "N"));

        
        query.orderBy(builder.asc(root.get("id")));

        return builder.and(restrictions.toArray(new Predicate[] {}));
	}

}
