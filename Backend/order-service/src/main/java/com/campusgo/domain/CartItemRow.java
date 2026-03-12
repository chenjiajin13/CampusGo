package com.campusgo.domain;

import lombok.Data;

@Data
public class CartItemRow {
    private Long id;
    private Long userId;
    private Long merchantId;
    private Long menuItemId;
    private Integer quantity;
    private Long unitPriceCents;
}
