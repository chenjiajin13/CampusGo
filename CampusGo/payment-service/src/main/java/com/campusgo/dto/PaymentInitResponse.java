package com.campusgo.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitResponse {
    private Long paymentId;
    private String payUrl;
    private String qrcodeUrl;
}
