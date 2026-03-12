package com.campusgo.domain;

import com.campusgo.enums.WalletBizType;
import com.campusgo.enums.WalletDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    private Long id;
    private Long accountId;
    private WalletBizType bizType;
    private WalletDirection direction;
    private Long amountCents;
    private Long orderId;
    private String idempotencyKey;
    private String remark;
    private Instant createdAt;
}
