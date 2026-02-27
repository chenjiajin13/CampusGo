package com.campusgo.service.impl;

import com.campusgo.domain.Payment;
import com.campusgo.dto.PaymentEvent;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import com.campusgo.mapper.PaymentMapper;
import com.campusgo.messaging.PaymentEventPublisher;
import com.campusgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper mapper;
    private final PaymentEventPublisher publisher;

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
        Payment payment = mapper.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Payment not found"));

        payment.setStatus(status);
        mapper.updateStatus(id, status);

        // 只在最终状态时发事件
        if (status == PaymentStatus.SUCCESS) {

            publisher.publish(new PaymentEvent(
                    UUID.randomUUID().toString(),
                    "PAYMENT_SUCCEEDED",
                    payment.getOrderId(),
                    payment.getId(),
                    payment.getAmountCents(),
                    System.currentTimeMillis()
            ));
        }

        if (status == PaymentStatus.FAILED) {

            publisher.publish(new PaymentEvent(
                    UUID.randomUUID().toString(),
                    "PAYMENT_FAILED",
                    payment.getOrderId(),
                    payment.getId(),
                    payment.getAmountCents(),
                    System.currentTimeMillis()
            ));
        }

        return payment;
    }

    @Override
    @Transactional
    public Payment refund(Long id, Long amountCents, String reason) {
        mapper.updateStatus(id, PaymentStatus.REFUNDED);
        return mapper.findById(id).orElse(null);
    }
}
