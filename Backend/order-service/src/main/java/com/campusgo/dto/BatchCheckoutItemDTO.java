package com.campusgo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchCheckoutItemDTO {
    private Long orderId;
    private Long merchantId;
    private Long runnerId;
    private String orderStatus;
    private Long amountCents;
    private String paymentStatus;
}
