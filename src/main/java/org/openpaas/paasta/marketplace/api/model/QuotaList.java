package org.openpaas.paasta.marketplace.api.model;

import lombok.Data;

import java.util.List;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-04-02
 */
@Data
public class QuotaList {

    private List<Quota> resources;
}
