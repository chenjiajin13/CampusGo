package com.campusgo.dto;


import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import lombok.*;


import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private Long amountCents;
    private String currency;
    private PaymentMethod method;
    private PaymentStatus status;
    private String providerTxnId;
    private Map<String, Object> extra;
}