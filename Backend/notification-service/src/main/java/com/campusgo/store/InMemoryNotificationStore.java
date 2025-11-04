package com.campusgo.store;


import com.campusgo.domain.Notification;
import com.campusgo.enums.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class InMemoryNotificationStore {
    private final Map<Long, Notification> byId = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> inboxIndex = new ConcurrentHashMap<>(); // key = targetType+":"+targetId
    private final AtomicLong idGen = new AtomicLong(7000);


    @PostConstruct
    public void initMock() {
// mock data: user:1 merchant:2000
        create(NotificationTargetType.USER, 1L, NotificationChannel.PUSH, "Welcome", "Hi, welcome to CampusGo!", Map.of());
        create(NotificationTargetType.USER, 1L, NotificationChannel.PUSH, "Order Created", "Your order #10001 has been created.", Map.of("orderId", 10001));
        create(NotificationTargetType.MERCHANT, 2000L, NotificationChannel.PUSH, "New Order", "A new order #10001 is placed.", Map.of("orderId", 10001));
    }


    public Notification create(NotificationTargetType targetType, Long targetId, NotificationChannel channel,
                               String title, String content, Map<String, Object> data) {
        long id = idGen.getAndIncrement();
        Instant now = Instant.now();
        Notification n = Notification.builder()
                .id(id).targetType(targetType).targetId(targetId)
                .channel(channel == null ? NotificationChannel.PUSH : channel)
                .title(title).content(content).data(data)
                .status(NotificationStatus.PENDING)
                .createdAt(now).build();
        byId.put(id, n);
        inboxKey(targetType, targetId).ifPresent(k -> inboxIndex.computeIfAbsent(k, __ -> new ArrayList<>()).add(id));
        return n;
    }


    public Optional<Notification> findById(Long id) { return Optional.ofNullable(byId.get(id)); }


    public List<Notification> listInbox(NotificationTargetType type, Long targetId) {
        String k = key(type, targetId);
        List<Long> ids = inboxIndex.getOrDefault(k, List.of());
        List<Notification> res = new ArrayList<>();
        for (Long id : ids) {
            Notification n = byId.get(id);
            if (n != null) res.add(n);
        }
        res.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        return res;
    }


    public Notification markSent(Long id) {
        Notification n = byId.get(id); if (n == null) throw new NoSuchElementException("Notification not found");
        n.setStatus(NotificationStatus.SENT); n.setSentAt(Instant.now()); return n;
    }


    private static String key(NotificationTargetType t, Long id) { return t.name()+":"+id; }
    private static Optional<String> inboxKey(NotificationTargetType t, Long id) { return Optional.ofNullable(id).map(v -> key(t, v)); }
}