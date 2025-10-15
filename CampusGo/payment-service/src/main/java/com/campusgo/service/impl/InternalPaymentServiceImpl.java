package com.campusgo.service.impl;


import com.campusgo.domain.Payment;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import com.campusgo.service.InternalPaymentService;
import com.campusgo.store.InMemoryPaymentStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
@RequiredArgsConstructor
public class InternalPaymentServiceImpl implements InternalPaymentService {
    private final InMemoryPaymentStore store;


    @Override public Payment initiate(Long orderId, Long userId, Long merchantId, Long amountCents, String currency, PaymentMethod method) { return store.create(orderId, userId, merchantId, amountCents, currency, method); }
    @Override public Optional<Payment> getByOrderId(Long orderId) { return store.findByOrderId(orderId); }
    @Override public Payment setStatus(Long paymentId, PaymentStatus status) { return store.updateStatus(paymentId, status); }
}