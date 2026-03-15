package com.campusgo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long merchantId;
    private Long menuItemId;
    private String itemName;
    private Long unitPriceCents;
    private Integer quantity;
    private Long subtotalCents;
    private Instant createdAt;
}
