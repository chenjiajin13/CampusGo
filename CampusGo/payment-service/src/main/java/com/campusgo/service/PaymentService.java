package com.campusgo.service;


import com.campusgo.domain.Payment;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;


import java.util.List;
import java.util.Optional;


/** for frontend use */
public interface PaymentService {
    Payment create(Long orderId, Long userId, Long merchantId, Long amountCents, String currency, PaymentMethod method);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> listByUser(Long userId);
    Payment updateStatus(Long id, PaymentStatus status);
    Payment refund(Long id, Long amountCents, String reason);
}