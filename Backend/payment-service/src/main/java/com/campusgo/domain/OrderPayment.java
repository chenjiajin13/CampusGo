package com.campusgo.domain;

import com.campusgo.enums.OrderPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayment {
    private Long id;
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private Long runnerId;
    private Long amountCents;
    private OrderPaymentStatus status;
    private String idempotencyKey;
    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;
}
