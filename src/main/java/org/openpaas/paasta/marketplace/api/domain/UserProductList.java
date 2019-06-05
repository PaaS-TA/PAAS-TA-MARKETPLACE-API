package org.openpaas.paasta.marketplace.api.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserProductList extends BaseEntity {

	@Column(name = "items")
	@ElementCollection(targetClass = String.class)
	private List<UserProduct> items;

}
