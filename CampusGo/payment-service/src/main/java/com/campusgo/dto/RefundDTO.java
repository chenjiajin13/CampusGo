package com.campusgo.dto;


import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundDTO {
    private Long paymentId;
    private String providerRefundId; // mock redund number
    private Long amountCents;
    private String status; // SUCCESS/FAILED
}