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

    private String createId;

    private String nameLike;

    @Override
    public Predicate toPredicate(Root<UserProduct> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> restrictions = new ArrayList<>();

        restrictions.add(builder.equal(root.get("deleteYn"), "N"));

        if (createId != null) {
            restrictions.add(builder.equal(root.get("createId"), createId));
        }
        if (nameLike != null) {
            restrictions.add(builder.like(root.get("productName"), "%" + nameLike + "%"));
        }

        query.orderBy(builder.asc(root.get("productName")));

        return builder.and(restrictions.toArray(new Predicate[] {}));
    }

}
