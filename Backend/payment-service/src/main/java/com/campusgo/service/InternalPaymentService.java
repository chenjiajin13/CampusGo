package com.campusgo.service;


import com.campusgo.domain.Payment;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;


import java.util.Optional;


/** for internal use */
public interface InternalPaymentService {
    Payment initiate(Long orderId, Long userId, Long merchantId, Long amountCents, String currency, PaymentMethod method);
    Optional<Payment> getByOrderId(Long orderId);
    Payment setStatus(Long paymentId, PaymentStatus status);
}