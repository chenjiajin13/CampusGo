package com.campusgo.dto;

public record OrderEvent (
        String eventId,
        String type,        // ORDER_PAID / ORDER_PAYMENT_FAILED
        Long orderId,
        Long userId,
        Long merchantId,
        Long amountCents,
        Long occurredAtMs
){
}
