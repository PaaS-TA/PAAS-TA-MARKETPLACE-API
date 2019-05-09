package org.openpaas.paasta.marketplace.api.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 판매자 프로필 모델
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-05-07
 */
@Data
@Entity
public class SellerProfile extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userId;

    @NotNull
    private String sellerName;

    @NotNull
    private String businessType;

    @Transient
    private List<CustomCode> businessTypeList;

    private String managerName;

    @NotNull
    private String email;

    private String homepageUrl;

}
