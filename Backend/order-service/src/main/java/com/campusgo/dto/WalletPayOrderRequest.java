package com.campusgo.dto;

import lombok.Data;

@Data
public class WalletPayOrderRequest {
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private Long runnerId;
    private Long amountCents;
    private String idempotencyKey;
}
