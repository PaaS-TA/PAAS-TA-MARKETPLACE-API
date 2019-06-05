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
public class UserProductSpecification implements Specification<UserProduct> {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String provisionStatus;
	private String nameLike;

    @Override
    public Predicate toPredicate(Root<UserProduct> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> restrictions = new ArrayList<>();

        if (userId != null) {
        	restrictions.add(builder.equal(root.get("userId"), userId));
        }

        if (nameLike != null) {
        	restrictions.add(builder.like(root.get("productName"), "%" + nameLike + "%"));
        }

        if (provisionStatus != null) {
        	restrictions.add(builder.equal(root.get("provisionStatus"), provisionStatus));
        }


        query.orderBy(builder.asc(root.get("productName")));

        return builder.and(restrictions.toArray(new Predicate[] {}));
    }

}
