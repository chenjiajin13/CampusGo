package com.campusgo.dto;

public record PaymentEvent(
        String eventId,
        String type,
        Long orderId,
        Long paymentId,
        Long amountCents,
        Long occurredAtMs
) {
}
