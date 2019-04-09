package org.openpaas.paasta.marketplace.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openpaas.paasta.marketplace.api.model.AbstractEntity.UseYn;
import org.springframework.data.jpa.domain.Specification;

import lombok.Data;

@Data
public class CategorySpecification implements Specification<Category> {

	private static final long serialVersionUID = 1L;

    private UseYn useYn = UseYn.Y;

    @Override
    public Predicate toPredicate(Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> restrictions = new ArrayList<>();
        if (useYn != null && useYn != UseYn.All) {
            restrictions.add(builder.equal(root.get("useYn"), useYn));
        }

        query.orderBy(builder.asc(root.get("seq")));

        return builder.and(restrictions.toArray(new Predicate[] {}));
    }

}
