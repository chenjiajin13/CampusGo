package com.campusgo.dto;

import com.campusgo.enums.OrderPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletOrderPaymentDTO {
    private Long orderId;
    private Long amountCents;
    private OrderPaymentStatus status;
    private Long merchantCents;
    private Long runnerCents;
}
