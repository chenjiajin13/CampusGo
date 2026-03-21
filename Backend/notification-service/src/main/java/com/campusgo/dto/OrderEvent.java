package com.campusgo.dto;

public record OrderEvent(
        String eventId,
        String type,
        Long orderId,
        Long userId,
        Long merchantId,
        Long amountCents,
        Long occurredAtMs
) {
}
