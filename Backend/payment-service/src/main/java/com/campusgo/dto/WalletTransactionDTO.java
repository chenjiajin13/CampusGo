package com.campusgo.dto;

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
public class WalletTransactionDTO {
    private Long id;
    private WalletBizType bizType;
    private WalletDirection direction;
    private Long amountCents;
    private Long orderId;
    private String remark;
    private Instant createdAt;
}
