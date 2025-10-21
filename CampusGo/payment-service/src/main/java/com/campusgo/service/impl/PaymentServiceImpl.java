package com.campusgo.service.impl;

import com.campusgo.domain.Payment;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import com.campusgo.mapper.PaymentMapper;
import com.campusgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper mapper;

    @Override
    @Transactional
    public Payment create(Long orderId, Long userId, Long merchantId,
                          Long amountCents, String currency, PaymentMethod method) {

        Payment p = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .merchantId(merchantId)
                .amountCents(amountCents)
                .currency(currency)
                .method(method)
                .status(PaymentStatus.PENDING)
                
                .providerTxnId(null)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mapper.insert(p);
        String txn = "MOCKTXN-" + p.getId();
        mapper.updateProviderTxnId(p.getId(), txn);
        p.setProviderTxnId(txn);
        return p;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return mapper.findByOrderId(orderId);
    }

    @Override
    public List<Payment> listByUser(Long userId) {
        return mapper.listByUser(userId);
    }

    @Override
    @Transactional
    public Payment updateStatus(Long id, PaymentStatus status) {
        mapper.updateStatus(id, status);
        return mapper.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Payment refund(Long id, Long amountCents, String reason) {
        mapper.updateStatus(id, PaymentStatus.REFUNDED);
        return mapper.findById(id).orElse(null);
    }
}
