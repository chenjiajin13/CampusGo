package com.campusgo.domain;

import com.campusgo.enums.OrderSettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSettlement {
    private Long id;
    private Long orderId;
    private Long merchantId;
    private Long runnerId;
    private Long grossCents;
    private Long merchantCents;
    private Long runnerCents;
    private OrderSettlementStatus status;
    private String idempotencyKey;
    private Instant settledAt;
    private Instant createdAt;
    private Instant updatedAt;
}
