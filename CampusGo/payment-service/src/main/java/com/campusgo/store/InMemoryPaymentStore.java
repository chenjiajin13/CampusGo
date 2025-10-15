package com.campusgo.store;
import com.campusgo.domain.Payment;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
@Component
public class InMemoryPaymentStore {
    private final Map<Long, Payment> byId = new ConcurrentHashMap<>();
    private final Map<Long, Long> orderId2PaymentId = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(5000);


    @PostConstruct
    public void initMock() {
// Mock data
        create(10001L, 1L, 2000L, 2999L, "CNY", PaymentMethod.Paypal);
        create(10002L, 1L, 2001L, 1599L, "CNY", PaymentMethod.WECHAT);
    }


    public Payment create(Long orderId, Long userId, Long merchantId, Long amountCents, String currency, PaymentMethod method) {
        long id = idGen.getAndIncrement();
        Instant now = Instant.now();
        Payment p = Payment.builder()
                .id(id)
                .orderId(orderId)
                .userId(userId)
                .merchantId(merchantId)
                .amountCents(amountCents)
                .currency(currency)
                .method(method)
                .status(PaymentStatus.PENDING)
                .providerTxnId("MOCKTXN-" + id)
                .createdAt(now)
                .updatedAt(now)
                .build();
        byId.put(id, p);
        orderId2PaymentId.put(orderId, id);
        return p;
    }


    public Optional<Payment> findById(Long id) { return Optional.ofNullable(byId.get(id)); }


    public Optional<Payment> findByOrderId(Long orderId) {
        Long pid = orderId2PaymentId.get(orderId);
        return pid == null ? Optional.empty() : findById(pid);
    }


    public List<Payment> listByUser(Long userId) {
        List<Payment> list = new ArrayList<>();
        for (Payment p : byId.values()) if (Objects.equals(p.getUserId(), userId)) list.add(p);
        return list;
    }


    public Payment updateStatus(Long id, PaymentStatus status) {
        Payment p = byId.get(id); if (p == null) throw new NoSuchElementException("Payment not found");
        p.setStatus(status); p.setUpdatedAt(Instant.now()); return p;
    }


    public Payment markRefunded(Long id) { return updateStatus(id, PaymentStatus.REFUNDED); }
}