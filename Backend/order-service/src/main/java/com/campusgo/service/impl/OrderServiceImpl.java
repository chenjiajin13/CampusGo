package com.campusgo.service.impl;

import com.campusgo.client.NotificationClient;
import com.campusgo.client.UserClient;
import com.campusgo.dto.UserDTO;
import com.campusgo.domain.Order;
import com.campusgo.mapper.OrderMapper;
import com.campusgo.service.OrderService;
import com.campusgo.dto.OrderDetail;
import com.campusgo.enums.TemplateKey;
import com.campusgo.dto.TemplateSendRequest;
import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationTargetType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final NotificationClient notificationClient;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    private static final long IDEM_TTL_MINUTES = 10;
    private static final long USER_CACHE_TTL_MINUTES = 10;
    private static final long LOCK_TTL_SECONDS = 8;
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) else return 0 end",
            Long.class
    );

    private String idemKey(Long userId, String idemKey) {
        return "campusgo:idem:order:create:" + userId + ":" + idemKey;
    }

    private String lockKey(Long userId) {
        return "campusgo:lock:order:create:" + userId;
    }


    @Override
    public OrderDetail getOrder(Long orderId) {
        Order o = orderMapper.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        UserDTO user = getUserCached(o.getUserId());
        return new OrderDetail(o.getId(), user, o.getStatus());
    }

    @Override
    @Transactional
    public OrderDetail createOrder(Long userId, Long merchantId, String address, String idemKey) {

        // 0) 幂等：如果有 idemKey，优先返回已创建的订单（避免重复插入&重复通知）
        if (idemKey != null && !idemKey.isBlank()) {
            String cachedOrderId = redis.opsForValue().get(idemKey(userId, idemKey));
            if (cachedOrderId != null) {
                return getOrder(Long.valueOf(cachedOrderId));
            }
        }

        // 1) 分布式锁：同一 userId 同时只能有一个创建流程
        String lk = lockKey(userId);
        String lv = UUID.randomUUID().toString();
        Boolean locked = redis.opsForValue().setIfAbsent(lk, lv, LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            // 这里你也可以返回 429（建议配合全局异常处理）
            throw new RuntimeException("TOO_MANY_REQUESTS");
        }

        try {
            // 2) 拿到锁后再 double-check 幂等（避免并发情况下重复创建）
            if (idemKey != null && !idemKey.isBlank()) {
                String cachedOrderId = redis.opsForValue().get(idemKey(userId, idemKey));
                if (cachedOrderId != null) {
                    return getOrder(Long.valueOf(cachedOrderId));
                }
            }

            // 3) 正常创建订单
            Order o = Order.builder()
                    .userId(userId)
                    .merchantId(merchantId)
                    .runnerId(null)
                    .amountCents(0L)
                    .status("CREATED")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            orderMapper.insert(o);  // 回写 id

            // 4) 缓存幂等映射：idemKey -> orderId
            if (idemKey != null && !idemKey.isBlank()) {
                redis.opsForValue().set(
                        idemKey(userId, idemKey),
                        String.valueOf(o.getId()),
                        IDEM_TTL_MINUTES,
                        TimeUnit.MINUTES
                );
            }

            // 5) 查询用户并发通知
            UserDTO user = getUserCached(userId);

            Map<String, Object> params = new HashMap<>();
            params.put("orderId", o.getId());

            notificationClient.sendTemplate(
                    TemplateSendRequest.builder()
                            .template(TemplateKey.ORDER_PLACED)
                            .targetType(NotificationTargetType.USER)
                            .targetId(userId)
                            .params(params)
                            .channel(NotificationChannel.PUSH)
                            .build()
            );

            notificationClient.sendTemplate(
                    TemplateSendRequest.builder()
                            .template(TemplateKey.ORDER_PLACED)
                            .targetType(NotificationTargetType.MERCHANT)
                            .targetId(merchantId)
                            .params(params)
                            .channel(NotificationChannel.PUSH)
                            .build()
            );

            return new OrderDetail(o.getId(), user, o.getStatus());

        } finally {
            // 6) 安全释放锁（只删自己的锁）
            redis.execute(UNLOCK_SCRIPT, Collections.singletonList(lk), lv);
        }
    }

    public List<Order> listAll() {
        return orderMapper.listAll();
    }

    private String userCacheKey(Long userId) {
        return "campusgo:cache:user:" + userId;
    }

    private UserDTO getUserCached(Long userId) {
        String key = userCacheKey(userId);

        // 1) cache hit
        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, UserDTO.class);
            } catch (Exception e) {
                // 缓存损坏/结构变更：删掉，走回源
                redis.delete(key);
            }
        }

        // 2) cache miss -> remote call
        UserDTO user = userClient.findById(userId);

        // 3) 回写缓存（失败不影响主流程）
        try {
            redis.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(user),
                    USER_CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        } catch (Exception ignore) {}

        return user;
    }


    @Transactional
    public void updateStatus(Long orderId, String status) {
        orderMapper.updateStatus(orderId, status);
    }
}
