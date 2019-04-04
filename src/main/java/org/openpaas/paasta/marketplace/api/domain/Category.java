package org.openpaas.paasta.marketplace.api.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends AbstractEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    private Long seq;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Software> softwareList;

    @PreRemove
    private void preRemove() {
        for (Software software : softwareList) {
            software.setCategory(null);
        }
    }

    public enum Direction {
        Up, Down,
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Category [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", seq=");
        builder.append(seq);
        builder.append("]");
        return builder.toString();
    }

}
