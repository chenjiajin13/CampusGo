package com.campusgo.service.impl;

import com.campusgo.domain.Payment;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import com.campusgo.mapper.PaymentMapper;
import com.campusgo.service.InternalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InternalPaymentServiceImpl implements InternalPaymentService {

    private final PaymentMapper mapper;

    @Override
    @Transactional
    public Payment initiate(Long orderId, Long userId, Long merchantId,
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
    public Optional<Payment> getByOrderId(Long orderId) {
        return mapper.findByOrderId(orderId);
    }

    @Override
    @Transactional
    public Payment setStatus(Long paymentId, PaymentStatus status) {
        mapper.updateStatus(paymentId, status);
        return mapper.findById(paymentId).orElse(null);
    }
}
