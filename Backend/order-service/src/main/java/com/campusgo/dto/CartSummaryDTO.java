package com.campusgo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryDTO {
    private Long merchantId;
    private List<CartItemDTO> items;
    private Integer totalQuantity;
    private Long totalCents;
}

