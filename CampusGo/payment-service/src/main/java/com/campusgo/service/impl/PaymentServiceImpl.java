package com.campusgo.service.impl;


import com.campusgo.domain.Payment;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import com.campusgo.service.PaymentService;
import com.campusgo.store.InMemoryPaymentStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final InMemoryPaymentStore store;


    @Override public Payment create(Long orderId, Long userId, Long merchantId, Long amountCents, String currency, PaymentMethod method) { return store.create(orderId, userId, merchantId, amountCents, currency, method); }
    @Override public Optional<Payment> findById(Long id) { return store.findById(id); }
    @Override public Optional<Payment> findByOrderId(Long orderId) { return store.findByOrderId(orderId); }
    @Override public List<Payment> listByUser(Long userId) { return store.listByUser(userId); }
    @Override public Payment updateStatus(Long id, PaymentStatus status) { return store.updateStatus(id, status); }
    @Override public Payment refund(Long id, Long amountCents, String reason) { return store.markRefunded(id); }
}