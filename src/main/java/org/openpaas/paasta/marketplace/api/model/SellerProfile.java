package org.openpaas.paasta.marketplace.api.model;

import lombok.Data;

import javax.persistence.*;

/**
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

    @Column(nullable = false)
    private String sellerName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupType groupType;

    private String managerName;

    @Column(nullable = false)
    private String email;

    private String homepageUrl;

    public enum GroupType {
        publicEnterprise, enterprise, individual ,etc
    }
}
