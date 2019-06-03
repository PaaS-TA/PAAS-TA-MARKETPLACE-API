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
public class SoftwareSpecification implements Specification<Product> {

	private static final long serialVersionUID = 1L;

//    private UseYn useYn = UseYn.Y;

    private Long categoryId;

    private Product.SwType type;

    private String createdId;

    private String nameLike;

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> restrictions = new ArrayList<>();
//        if (useYn != null && useYn != UseYn.All) {
//            restrictions.add(builder.equal(root.get("useYn"), useYn));
//        }
        if (categoryId != null) {
            restrictions.add(builder.equal(root.get("category").get("id"), categoryId));
        }
        if (type != null) {
            restrictions.add(builder.equal(root.get("type"), type));
        }
        if (createdId != null) {
            restrictions.add(builder.equal(root.get("createdId"), createdId));
        }
        if (nameLike != null) {
            restrictions.add(builder.like(root.get("name"), "%" + nameLike + "%"));
        }

        query.orderBy(builder.asc(root.get("name")));

        return builder.and(restrictions.toArray(new Predicate[] {}));
    }

}
