package com.campusgo.dto;

public record PaymentEvent (
                           String eventId,     // UUID
                           String type,        // PAYMENT_SUCCEEDED / PAYMENT_FAILED
                           Long orderId,
                           Long paymentId,
                           Long amountCents,
                           Long occurredAtMs
){ }
