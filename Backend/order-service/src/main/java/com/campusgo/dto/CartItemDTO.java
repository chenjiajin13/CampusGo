package com.campusgo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long menuItemId;
    private String name;
    private Long unitPriceCents;
    private Integer quantity;
    private Long subtotalCents;
}

