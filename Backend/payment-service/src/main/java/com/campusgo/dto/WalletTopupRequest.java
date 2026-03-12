package com.campusgo.dto;

import lombok.Data;

@Data
public class WalletTopupRequest {
    private Long amountCents;
    private String idempotencyKey;
    private String remark;
}
