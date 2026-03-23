package com.campusgo.dto;

import lombok.Data;

@Data
public class OrderItemDetailDTO {
    private Long menuItemId;
    private String itemName;
    private Long unitPriceCents;
    private Integer quantity;
    private Long subtotalCents;
}

