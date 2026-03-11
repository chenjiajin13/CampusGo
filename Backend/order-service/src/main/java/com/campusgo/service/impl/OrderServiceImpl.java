package com.campusgo.service.impl;

import com.campusgo.client.MerchantClient;
import com.campusgo.client.NotificationClient;
import com.campusgo.client.PaymentClient;
import com.campusgo.client.UserClient;
import com.campusgo.domain.Order;
import com.campusgo.dto.CartItemDTO;
import com.campusgo.dto.CartItemRequest;
import com.campusgo.dto.CartSummaryDTO;
import com.campusgo.dto.MenuItemDTO;
import com.campusgo.dto.OrderDetail;
import com.campusgo.dto.PaymentCreateRequest;
import com.campusgo.dto.PaymentDTO;
import com.campusgo.dto.QuickOrderRequest;
import com.campusgo.dto.TemplateSendRequest;
import com.campusgo.dto.UserDTO;
import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationTargetType;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.TemplateKey;
import com.campusgo.mapper.OrderMapper;
import com.campusgo.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final MerchantClient merchantClient;
    private final PaymentClient paymentClient;
    private final NotificationClient notificationClient;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    private static final long IDEM_TTL_MINUTES = 10;
    private static final long USER_CACHE_TTL_MINUTES = 10;
    private static final long LOCK_TTL_SECONDS = 8;
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class
    );

    @Data
    private static class CartSnapshot {
        private Long merchantId;
        private Map<Long, Integer> quantities = new LinkedHashMap<>();
    }

    private String idemKey(Long userId, String idemKey) {
        return "campusgo:idem:order:create:" + userId + ":" + idemKey;
    }

    private String lockKey(Long userId) {
        return "campusgo:lock:order:create:" + userId;
    }

    private String userCacheKey(Long userId) {
        return "campusgo:cache:user:" + userId;
    }

    private String cartKey(Long userId) {
        return "campusgo:cart:user:" + userId;
    }

    @Override
    public OrderDetail getOrder(Long orderId) {
        Order o = orderMapper.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        UserDTO user = getUserCached(o.getUserId());
        return new OrderDetail(o.getId(), user, o.getStatus(), o.getAmountCents(), null);
    }

    @Override
    @Transactional
    public OrderDetail createOrder(Long userId, Long merchantId, String address, String idemKey) {
        if (idemKey != null && !idemKey.isBlank()) {
            String cachedOrderId = redis.opsForValue().get(idemKey(userId, idemKey));
            if (cachedOrderId != null) {
                return getOrder(Long.valueOf(cachedOrderId));
            }
        }

        String lk = lockKey(userId);
        String lv = UUID.randomUUID().toString();
        Boolean locked = redis.opsForValue().setIfAbsent(lk, lv, LOCK_TTL_SECONDS, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            throw new RuntimeException("TOO_MANY_REQUESTS");
        }

        try {
            if (idemKey != null && !idemKey.isBlank()) {
                String cachedOrderId = redis.opsForValue().get(idemKey(userId, idemKey));
                if (cachedOrderId != null) {
                    return getOrder(Long.valueOf(cachedOrderId));
                }
            }

            Order o = Order.builder()
                    .userId(userId)
                    .merchantId(merchantId)
                    .runnerId(null)
                    .amountCents(0L)
                    .status("CREATED")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            orderMapper.insert(o);

            if (idemKey != null && !idemKey.isBlank()) {
                redis.opsForValue().set(idemKey(userId, idemKey), String.valueOf(o.getId()), IDEM_TTL_MINUTES, TimeUnit.MINUTES);
            }

            UserDTO user = getUserCached(userId);
            notifyOrderPlaced(o.getId(), userId, merchantId);

            return new OrderDetail(o.getId(), user, o.getStatus(), o.getAmountCents(), null);
        } finally {
            redis.execute(UNLOCK_SCRIPT, Collections.singletonList(lk), lv);
        }
    }

    @Override
    public CartSummaryDTO addToCart(Long userId, CartItemRequest req) {
        if (req == null || req.getMerchantId() == null || req.getMenuItemId() == null || req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new IllegalArgumentException("INVALID_CART_ITEM");
        }

        CartSnapshot snapshot = readCart(userId);
        if (snapshot == null) {
            snapshot = new CartSnapshot();
            snapshot.setMerchantId(req.getMerchantId());
        }

        if (snapshot.getMerchantId() != null && !snapshot.getMerchantId().equals(req.getMerchantId())) {
            throw new IllegalArgumentException("CART_SINGLE_MERCHANT_ONLY");
        }

        Map<Long, MenuItemDTO> menuMap = menuMap(req.getMerchantId());
        MenuItemDTO menuItem = menuMap.get(req.getMenuItemId());
        if (menuItem == null || Boolean.FALSE.equals(menuItem.getEnabled())) {
            throw new NoSuchElementException("MENU_ITEM_NOT_FOUND");
        }

        snapshot.getQuantities().merge(req.getMenuItemId(), req.getQuantity(), Integer::sum);
        snapshot.setMerchantId(req.getMerchantId());
        saveCart(userId, snapshot);
        return toSummary(snapshot, menuMap);
    }

    @Override
    public CartSummaryDTO getCart(Long userId) {
        CartSnapshot snapshot = readCart(userId);
        if (snapshot == null || snapshot.getMerchantId() == null || snapshot.getQuantities().isEmpty()) {
            return emptyCart();
        }
        return toSummary(snapshot, menuMap(snapshot.getMerchantId()));
    }

    @Override
    public CartSummaryDTO removeFromCart(Long userId, Long menuItemId) {
        CartSnapshot snapshot = readCart(userId);
        if (snapshot == null || snapshot.getQuantities().isEmpty()) {
            return emptyCart();
        }
        snapshot.getQuantities().remove(menuItemId);
        if (snapshot.getQuantities().isEmpty()) {
            clearCart(userId);
            return emptyCart();
        }
        saveCart(userId, snapshot);
        return toSummary(snapshot, menuMap(snapshot.getMerchantId()));
    }

    @Override
    public void clearCart(Long userId) {
        redis.delete(cartKey(userId));
    }

    @Override
    @Transactional
    public OrderDetail checkoutCart(Long userId, String address, String idemKey, Boolean autoPay) {
        CartSnapshot snapshot = readCart(userId);
        if (snapshot == null || snapshot.getMerchantId() == null || snapshot.getQuantities().isEmpty()) {
            throw new IllegalArgumentException("CART_EMPTY");
        }

        QuickOrderRequest req = new QuickOrderRequest();
        req.setMerchantId(snapshot.getMerchantId());
        req.setAddress(address);
        req.setAutoPay(autoPay);
        List<CartItemRequest> items = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : snapshot.getQuantities().entrySet()) {
            items.add(new CartItemRequest(snapshot.getMerchantId(), e.getKey(), e.getValue()));
        }
        req.setItems(items);

        OrderDetail detail = quickOrder(userId, req, idemKey);
        clearCart(userId);
        return detail;
    }

    @Override
    @Transactional
    public OrderDetail quickOrder(Long userId, QuickOrderRequest req, String idemKey) {
        if (req == null || req.getMerchantId() == null || req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("INVALID_ORDER_ITEMS");
        }

        Map<Long, MenuItemDTO> menuMap = menuMap(req.getMerchantId());
        long totalCents = 0L;
        for (CartItemRequest item : req.getItems()) {
            if (item.getMenuItemId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("INVALID_ORDER_ITEM_QUANTITY");
            }
            MenuItemDTO menuItem = menuMap.get(item.getMenuItemId());
            if (menuItem == null || Boolean.FALSE.equals(menuItem.getEnabled())) {
                throw new NoSuchElementException("MENU_ITEM_NOT_FOUND");
            }
            totalCents += menuItem.getPriceCents() * item.getQuantity();
        }

        Order o = Order.builder()
                .userId(userId)
                .merchantId(req.getMerchantId())
                .runnerId(null)
                .amountCents(totalCents)
                .status("CREATED")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        orderMapper.insert(o);

        if (idemKey != null && !idemKey.isBlank()) {
            redis.opsForValue().set(idemKey(userId, idemKey), String.valueOf(o.getId()), IDEM_TTL_MINUTES, TimeUnit.MINUTES);
        }

        UserDTO user = getUserCached(userId);
        notifyOrderPlaced(o.getId(), userId, req.getMerchantId());

        String paymentStatus = null;
        if (Boolean.TRUE.equals(req.getAutoPay())) {
            PaymentDTO payment = paymentClient.initiate(
                    PaymentCreateRequest.builder()
                            .orderId(o.getId())
                            .userId(userId)
                            .merchantId(req.getMerchantId())
                            .amountCents(totalCents)
                            .currency("SGD")
                            .method(PaymentMethod.WALLET)
                            .build()
            );
            paymentStatus = payment == null || payment.getStatus() == null ? null : payment.getStatus().name();
        }

        return new OrderDetail(o.getId(), user, o.getStatus(), totalCents, paymentStatus);
    }

    public List<Order> listAll() {
        return orderMapper.listAll();
    }

    @Transactional
    public void updateStatus(Long orderId, String status) {
        orderMapper.updateStatus(orderId, status);
    }

    private void notifyOrderPlaced(Long orderId, Long userId, Long merchantId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);

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
    }

    private CartSummaryDTO emptyCart() {
        return CartSummaryDTO.builder()
                .merchantId(null)
                .items(Collections.emptyList())
                .totalQuantity(0)
                .totalCents(0L)
                .build();
    }

    private CartSnapshot readCart(Long userId) {
        String raw = redis.opsForValue().get(cartKey(userId));
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(raw, CartSnapshot.class);
        } catch (Exception e) {
            redis.delete(cartKey(userId));
            return null;
        }
    }

    private void saveCart(Long userId, CartSnapshot snapshot) {
        try {
            redis.opsForValue().set(cartKey(userId), objectMapper.writeValueAsString(snapshot), 2, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new RuntimeException("CART_SAVE_FAILED", e);
        }
    }

    private Map<Long, MenuItemDTO> menuMap(Long merchantId) {
        List<MenuItemDTO> menu = merchantClient.menu(merchantId);
        Map<Long, MenuItemDTO> map = new LinkedHashMap<>();
        if (menu != null) {
            for (MenuItemDTO item : menu) {
                if (item != null && item.getId() != null) {
                    map.put(item.getId(), item);
                }
            }
        }
        return map;
    }

    private CartSummaryDTO toSummary(CartSnapshot snapshot, Map<Long, MenuItemDTO> menuMap) {
        List<CartItemDTO> items = new ArrayList<>();
        long totalCents = 0L;
        int totalQty = 0;

        for (Map.Entry<Long, Integer> entry : snapshot.getQuantities().entrySet()) {
            MenuItemDTO menuItem = menuMap.get(entry.getKey());
            if (menuItem == null || Boolean.FALSE.equals(menuItem.getEnabled())) {
                continue;
            }
            int qty = entry.getValue();
            long subtotal = menuItem.getPriceCents() * qty;
            totalCents += subtotal;
            totalQty += qty;
            items.add(CartItemDTO.builder()
                    .menuItemId(menuItem.getId())
                    .name(menuItem.getName())
                    .unitPriceCents(menuItem.getPriceCents())
                    .quantity(qty)
                    .subtotalCents(subtotal)
                    .build());
        }

        return CartSummaryDTO.builder()
                .merchantId(snapshot.getMerchantId())
                .items(items)
                .totalQuantity(totalQty)
                .totalCents(totalCents)
                .build();
    }

    private UserDTO getUserCached(Long userId) {
        String key = userCacheKey(userId);
        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, UserDTO.class);
            } catch (Exception e) {
                redis.delete(key);
            }
        }

        UserDTO user = userClient.findById(userId);
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(user), USER_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception ignore) {
        }
        return user;
    }
}
