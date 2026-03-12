package com.campusgo.dto;

import lombok.Data;

@Data
public class WalletOrderPaymentDTO {
    private Long orderId;
    private Long amountCents;
    private String status;
    private Long merchantCents;
    private Long runnerCents;
}
