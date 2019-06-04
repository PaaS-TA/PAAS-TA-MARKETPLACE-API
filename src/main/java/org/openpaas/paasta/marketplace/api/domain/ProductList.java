package org.openpaas.paasta.marketplace.api.domain;

import lombok.Data;

import java.util.List;

/**
 * 상품 List 도메인
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-06-04
 */
@Data
public class ProductList {

    List<Product> items;

    int total;
    int start;
    int display;
    int page;
    int size;
    int totalPages;
    long totalElements;
    boolean isLast;

}
