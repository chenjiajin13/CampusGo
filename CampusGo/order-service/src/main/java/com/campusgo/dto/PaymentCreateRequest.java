package com.campusgo.dto;


import com.campusgo.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequest {
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private Long amountCents;
    private String currency; // eg.SGD CNY
    private PaymentMethod method; // WALLET/CREDIT_CARD/WECHAT/PAYPAL
}