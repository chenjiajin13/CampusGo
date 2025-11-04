package com.campusgo.store;

import com.campusgo.domain.Order;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryOrderStore {

    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(10000);

    @PostConstruct
    public void initMock() {
        // 可选：初始化一条订单
        create(1L, 2000L, 2999L);
    }

    public Order create(Long userId, Long merchantId, Long amountCents) {
        long id = idGen.getAndIncrement();
        Order o = Order.builder()
                .id(id)
                .userId(userId)
                .merchantId(merchantId)
                .amountCents(amountCents)
                .status("CREATED")
                .createdAt(Instant.now())
                .build();
        store.put(id, o);
        return o;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Order> listAll() {
        return new ArrayList<>(store.values());
    }

    public void updateStatus(Long id, String newStatus) {
        Order o = store.get(id);
        if (o != null) {
            o.setStatus(newStatus);
        }
    }
}
