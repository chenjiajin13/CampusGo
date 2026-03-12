package com.campusgo.dto;

import lombok.Data;

@Data
public class WalletSettleRequest {
    private Long orderId;
    private Long merchantId;
    private Long runnerId;
    private Long amountCents;
    private String idempotencyKey;
}
