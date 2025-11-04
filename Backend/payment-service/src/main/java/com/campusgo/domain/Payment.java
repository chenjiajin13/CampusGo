package com.campusgo.domain;


import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import lombok.*;


import java.time.Instant;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long id;
    private Long orderId;
    private Long userId; // 下单用户
    private Long merchantId; // 商户
    private Long amountCents; // 以分为单位
    private String currency; // e.g. SGD, CNY, USD
    private PaymentMethod method;
    private PaymentStatus status;
    private String providerTxnId; // 第三方渠道交易号（mock）
    private Map<String, Object> extra; // 任意附加信息
    private Instant createdAt;
    private Instant updatedAt;
}