package com.campusgo.mapper;


import com.campusgo.domain.Payment;
import com.campusgo.dto.PaymentDTO;


public class PaymentMapper {
    public static PaymentDTO toDTO(Payment p) {
        if (p == null) return null;
        return PaymentDTO.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .userId(p.getUserId())
                .merchantId(p.getMerchantId())
                .amountCents(p.getAmountCents())
                .currency(p.getCurrency())
                .method(p.getMethod())
                .status(p.getStatus())
                .providerTxnId(p.getProviderTxnId())
                .extra(p.getExtra())
                .build();
    }
}