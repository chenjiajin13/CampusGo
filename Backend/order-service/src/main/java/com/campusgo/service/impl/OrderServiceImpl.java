package com.campusgo.service.impl;

import com.campusgo.client.MerchantClient;
import com.campusgo.client.NotificationClient;
import com.campusgo.client.PaymentClient;
import com.campusgo.client.RunnerClient;
import com.campusgo.client.UserClient;
import com.campusgo.domain.Order;
import com.campusgo.domain.CartItemRow;
import com.campusgo.domain.OrderItem;
import com.campusgo.dto.BatchCheckoutItemDTO;
import com.campusgo.dto.BatchCheckoutResponse;
import com.campusgo.dto.CartItemDTO;
import com.campusgo.dto.CartItemRequest;
import com.campusgo.dto.CartSummaryDTO;
import com.campusgo.dto.MerchantAnalyticsDTO;
import com.campusgo.dto.MerchantDailyRevenueDTO;
import com.campusgo.dto.MerchantDailyRevenueRow;
import com.campusgo.dto.MerchantItemShareDTO;
import com.campusgo.dto.MenuItemDTO;
import com.campusgo.dto.OrderDetail;
import com.campusgo.dto.QuickOrderRequest;
import com.campusgo.dto.RunnerDTO;
import com.campusgo.dto.TemplateSendRequest;
import com.campusgo.dto.UpdateStatusRequest;
import com.campusgo.dto.UserDTO;
import com.campusgo.dto.WalletOrderPaymentDTO;
import com.campusgo.dto.WalletPayOrderRequest;
import com.campusgo.dto.WalletSettleRequest;
import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationTargetType;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.enums.TemplateKey;
import com.campusgo.mapper.CartItemMapper;
import com.campusgo.mapper.OrderMapper;
import com.campusgo.mapper.OrderItemMapper;
import com.campusgo.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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
    private final RunnerClient runnerClient;
    private final CartItemMapper cartItemMapper;
    private final OrderItemMapper orderItemMapper;
    private final StringRedisTemplate redis;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

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
        private Map<Long, Long> unitPrices = new LinkedHashMap<>();
        private Map<Long, Long> merchantByItem = new LinkedHashMap<>();
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

    @Override
    public OrderDetail getOrder(Long orderId) {
        Order o = orderMapper.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        return buildOrderDetail(o, null);
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
            assignRunnerIfAvailable(o);

            if (idemKey != null && !idemKey.isBlank()) {
                redis.opsForValue().set(idemKey(userId, idemKey), String.valueOf(o.getId()), IDEM_TTL_MINUTES, TimeUnit.MINUTES);
            }

            notifyOrderPlaced(o.getId(), userId, merchantId);
            notifyOrderAssignedIfAny(o);
            return buildOrderDetail(o, null);
        } finally {
            redis.execute(UNLOCK_SCRIPT, Collections.singletonList(lk), lv);
        }
    }

    @Override
    public CartSummaryDTO addToCart(Long userId, CartItemRequest req) {
        if (req == null || req.getMerchantId() == null || req.getMenuItemId() == null || req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new IllegalArgumentException("INVALID_CART_ITEM");
        }

        Map<Long, MenuItemDTO> menuMap = menuMap(req.getMerchantId());
        MenuItemDTO menuItem = menuMap.get(req.getMenuItemId());
        if (menuItem == null || Boolean.FALSE.equals(menuItem.getEnabled())) {
            throw new NoSuchElementException("MENU_ITEM_NOT_FOUND");
        }

        cartItemMapper.upsertAdd(
                userId,
                req.getMerchantId(),
                req.getMenuItemId(),
                req.getQuantity(),
                menuItem.getPriceCents()
        );
        return toSummary(readCart(userId));
    }

    @Override
    public CartSummaryDTO getCart(Long userId) {
        CartSnapshot snapshot = readCart(userId);
        if (snapshot == null || snapshot.getQuantities().isEmpty()) {
            return emptyCart();
        }
        return toSummary(snapshot);
    }

    @Override
    public CartSummaryDTO removeFromCart(Long userId, Long menuItemId) {
        CartSnapshot snapshot = readCart(userId);
        if (snapshot == null || snapshot.getQuantities().isEmpty()) {
            return emptyCart();
        }
        cartItemMapper.deleteItem(userId, menuItemId);
        CartSnapshot after = readCart(userId);
        if (after == null || after.getQuantities().isEmpty()) {
            return emptyCart();
        }
        return toSummary(after);
    }

    @Override
    public void clearCart(Long userId) {
        cartItemMapper.deleteByUser(userId);
    }

    @Override
    @Transactional
    public OrderDetail checkoutCart(Long userId, String address, String idemKey, Boolean autoPay) {
        CartSnapshot snapshot = readCart(userId);
        if (snapshot == null || snapshot.getQuantities().isEmpty()) {
            throw new IllegalArgumentException("CART_EMPTY");
        }
        QuickOrderRequest req = new QuickOrderRequest();
        Long merchantId = snapshot.getMerchantId();
        if (merchantId == null) {
            throw new IllegalArgumentException("MULTI_MERCHANT_CHECKOUT_USE_BATCH");
        }
        req.setMerchantId(merchantId);
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
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemRequest item : req.getItems()) {
            if (item.getMenuItemId() == null || item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("INVALID_ORDER_ITEM_QUANTITY");
            }
            MenuItemDTO menuItem = menuMap.get(item.getMenuItemId());
            if (menuItem == null || Boolean.FALSE.equals(menuItem.getEnabled())) {
                throw new NoSuchElementException("MENU_ITEM_NOT_FOUND");
            }
            long subtotal = menuItem.getPriceCents() * item.getQuantity();
            totalCents += subtotal;
            orderItems.add(OrderItem.builder()
                    .merchantId(req.getMerchantId())
                    .menuItemId(item.getMenuItemId())
                    .itemName(menuItem.getName())
                    .unitPriceCents(menuItem.getPriceCents())
                    .quantity(item.getQuantity())
                    .subtotalCents(subtotal)
                    .build());
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
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(o.getId());
        }
        if (!orderItems.isEmpty()) {
            orderItemMapper.insertBatch(orderItems);
        }
        assignRunnerIfAvailable(o);

        if (idemKey != null && !idemKey.isBlank()) {
            redis.opsForValue().set(idemKey(userId, idemKey), String.valueOf(o.getId()), IDEM_TTL_MINUTES, TimeUnit.MINUTES);
        }

        notifyOrderPlaced(o.getId(), userId, req.getMerchantId());
        notifyOrderAssignedIfAny(o);

        String paymentStatus = null;
        if (Boolean.TRUE.equals(req.getAutoPay())) {
            WalletPayOrderRequest payReq = new WalletPayOrderRequest();
            payReq.setOrderId(o.getId());
            payReq.setUserId(userId);
            payReq.setMerchantId(req.getMerchantId());
            payReq.setRunnerId(o.getRunnerId());
            payReq.setAmountCents(totalCents);
            payReq.setIdempotencyKey("wallet-pay:" + o.getId());
            WalletOrderPaymentDTO payment = paymentClient.walletPayOrder(payReq);
            paymentStatus = payment == null ? null : payment.getStatus();
        }

        return buildOrderDetail(o, paymentStatus);
    }

    @Override
    @Transactional
    public BatchCheckoutResponse checkoutCartBatch(Long userId, String address, String idemKey, Boolean autoPay) {
        CartSnapshot snapshot = readCart(userId);
        if (snapshot == null || snapshot.getQuantities().isEmpty()) {
            throw new IllegalArgumentException("CART_EMPTY");
        }

        Map<Long, List<CartItemRequest>> grouped = new LinkedHashMap<>();
        for (Map.Entry<Long, Integer> e : snapshot.getQuantities().entrySet()) {
            Long menuItemId = e.getKey();
            Integer qty = e.getValue();
            Long merchantId = snapshot.getMerchantByItem().get(menuItemId);
            if (merchantId == null || qty == null || qty <= 0) {
                continue;
            }
            grouped.computeIfAbsent(merchantId, k -> new ArrayList<>())
                    .add(new CartItemRequest(merchantId, menuItemId, qty));
        }
        if (grouped.isEmpty()) {
            throw new IllegalArgumentException("CART_EMPTY");
        }

        List<BatchCheckoutItemDTO> created = new ArrayList<>();
        long total = 0L;
        boolean allPaid = true;
        int idx = 0;
        for (Map.Entry<Long, List<CartItemRequest>> g : grouped.entrySet()) {
            QuickOrderRequest req = new QuickOrderRequest();
            req.setMerchantId(g.getKey());
            req.setAddress(address);
            req.setItems(g.getValue());
            req.setAutoPay(autoPay);

            String merchantIdem = (idemKey == null || idemKey.isBlank()) ? null : idemKey + ":" + g.getKey() + ":" + idx;
            OrderDetail d = quickOrder(userId, req, merchantIdem);
            idx++;
            total += d.getAmountCents() == null ? 0L : d.getAmountCents();
            boolean paid = "PAID_ESCROW".equalsIgnoreCase(d.getPaymentStatus())
                    || "SETTLED".equalsIgnoreCase(d.getPaymentStatus())
                    || "SUCCESS".equalsIgnoreCase(d.getPaymentStatus());
            allPaid = allPaid && (Boolean.TRUE.equals(autoPay) ? paid : true);
            Order persisted = orderMapper.findById(d.getOrderId()).orElse(null);
            created.add(BatchCheckoutItemDTO.builder()
                    .orderId(d.getOrderId())
                    .merchantId(g.getKey())
                    .runnerId(persisted == null ? null : persisted.getRunnerId())
                    .orderStatus(d.getStatus())
                    .amountCents(d.getAmountCents())
                    .paymentStatus(d.getPaymentStatus())
                    .build());
        }

        clearCart(userId);
        return BatchCheckoutResponse.builder()
                .orderCount(created.size())
                .totalAmountCents(total)
                .allPaid(Boolean.TRUE.equals(autoPay) ? allPaid : false)
                .orders(created)
                .build();
    }

    public List<Order> listAll() {
        return orderMapper.listAll();
    }

    @Override
    public List<OrderDetail> listMyOrders(Long userId) {
        return toOrderDetails(orderMapper.listByUserId(userId));
    }

    @Override
    public List<OrderDetail> listMerchantOrders(Long merchantId) {
        return toOrderDetails(orderMapper.listByMerchantId(merchantId));
    }

    @Override
    public MerchantAnalyticsDTO getMerchantAnalytics(Long merchantId, String weekStart) {
        LocalDate start = parseOrCurrentWeekStart(weekStart);
        LocalDate end = start.plusDays(6);

        LocalDateTime startAt = start.atStartOfDay();
        LocalDateTime endAtExclusive = end.plusDays(1).atStartOfDay();

        List<MerchantDailyRevenueRow> rows = orderMapper.statDailyRevenueByMerchant(merchantId, startAt, endAtExclusive);
        Map<String, Long> byDay = new HashMap<>();
        if (rows != null) {
            for (MerchantDailyRevenueRow row : rows) {
                if (row == null || row.getDayKey() == null) {
                    continue;
                }
                byDay.put(row.getDayKey(), row.getAmountCents() == null ? 0L : row.getAmountCents());
            }
        }

        List<MerchantDailyRevenueDTO> daily = new ArrayList<>();
        long weekRevenue = 0L;
        for (int i = 0; i < 7; i++) {
            LocalDate d = start.plusDays(i);
            String key = d.toString();
            long amount = byDay.getOrDefault(key, 0L);
            weekRevenue += amount;
            daily.add(new MerchantDailyRevenueDTO(key, amount));
        }

        Long lifetime = orderMapper.statRevenueByMerchant(merchantId);
        Long annual = orderMapper.statRevenueByMerchantSince(merchantId, LocalDateTime.now().minusYears(1));
        Long completedCount = orderMapper.statCompletedOrderCountByMerchant(merchantId);
        List<MerchantItemShareDTO> itemShare = orderMapper.statItemShareByMerchant(merchantId);

        return MerchantAnalyticsDTO.builder()
                .weekStart(start.toString())
                .weekEnd(end.toString())
                .selectedWeekRevenueCents(weekRevenue)
                .lifetimeRevenueCents(lifetime == null ? 0L : lifetime)
                .annualRevenueCents(annual == null ? 0L : annual)
                .completedOrderCount(completedCount == null ? 0L : completedCount)
                .dailyRevenue(daily)
                .itemShare(itemShare == null ? Collections.emptyList() : itemShare)
                .build();
    }

    @Override
    public List<OrderDetail> listRunnerOrders(Long runnerId) {
        return toOrderDetails(orderMapper.listByRunnerId(runnerId));
    }

    @Override
    @Transactional
    public OrderDetail completeByRunner(Long runnerId, Long orderId) {
        Order o = orderMapper.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));
        if (o.getRunnerId() == null || !o.getRunnerId().equals(runnerId)) {
            throw new IllegalArgumentException("RUNNER_NOT_ASSIGNED_TO_ORDER");
        }
        if ("ORDER_DELIVERED".equalsIgnoreCase(o.getStatus())) {
            return buildOrderDetail(o, null);
        }
        if (!"ORDER_ASSIGNED".equalsIgnoreCase(o.getStatus()) && !"ORDER_PICKED_UP".equalsIgnoreCase(o.getStatus())) {
            throw new IllegalArgumentException("ORDER_NOT_COMPLETABLE");
        }
        String paymentStatus = null;
        try {
            WalletSettleRequest settleReq = new WalletSettleRequest();
            settleReq.setOrderId(orderId);
            settleReq.setMerchantId(o.getMerchantId());
            settleReq.setRunnerId(runnerId);
            settleReq.setAmountCents(o.getAmountCents());
            settleReq.setIdempotencyKey("wallet-settle:" + orderId);
            WalletOrderPaymentDTO settle = paymentClient.walletSettle(settleReq);
            paymentStatus = settle == null ? null : settle.getStatus();
        } catch (Exception ex) {
            paymentStatus = "SETTLEMENT_PENDING";
        }

        orderMapper.updateStatus(orderId, "ORDER_DELIVERED");
        try {
            refreshRunnerWorkloadStatus(runnerId);
        } catch (Exception ignore) {
        }
        Order updated = orderMapper.findById(orderId).orElseThrow();
        try {
            notifyOrderDelivered(updated.getId(), updated.getUserId(), updated.getMerchantId(), updated.getRunnerId());
        } catch (Exception ignore) {
        }
        return buildOrderDetail(updated, paymentStatus);
    }

    @Transactional
    public void updateStatus(Long orderId, String status) {
        orderMapper.updateStatus(orderId, status);
    }

    private void notifyOrderPlaced(Long orderId, Long userId, Long merchantId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);

        sendTemplateToTarget(TemplateKey.ORDER_PLACED, NotificationTargetType.USER, userId, params);
        sendTemplateToTarget(TemplateKey.ORDER_PLACED, NotificationTargetType.MERCHANT, merchantId, params);
    }

    private void notifyOrderAssignedIfAny(Order o) {
        if (o.getRunnerId() == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", o.getId());
        params.put("runnerId", o.getRunnerId());

        sendTemplateToTarget(TemplateKey.ORDER_ASSIGNED, NotificationTargetType.USER, o.getUserId(), params);
        sendTemplateToTarget(TemplateKey.ORDER_ASSIGNED, NotificationTargetType.MERCHANT, o.getMerchantId(), params);
        sendTemplateToTarget(TemplateKey.ORDER_ASSIGNED, NotificationTargetType.RUNNER, o.getRunnerId(), params);
    }

    private void notifyOrderDelivered(Long orderId, Long userId, Long merchantId, Long runnerId) {
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);

        sendTemplateToTarget(TemplateKey.ORDER_DELIVERED, NotificationTargetType.USER, userId, params);
        sendTemplateToTarget(TemplateKey.ORDER_DELIVERED, NotificationTargetType.MERCHANT, merchantId, params);
        if (runnerId != null) {
            sendTemplateToTarget(TemplateKey.ORDER_DELIVERED, NotificationTargetType.RUNNER, runnerId, params);
        }
    }

    private void sendTemplateToTarget(TemplateKey template, NotificationTargetType targetType, Long targetId, Map<String, Object> params) {
        try {
            notificationClient.sendTemplate(
                    TemplateSendRequest.builder()
                            .template(template)
                            .targetType(targetType)
                            .targetId(targetId)
                            .params(params)
                            .channel(NotificationChannel.PUSH)
                            .build()
            );
        } catch (Exception ignore) {
        }
        try {
            notificationClient.sendTemplate(
                    TemplateSendRequest.builder()
                            .template(template)
                            .targetType(targetType)
                            .targetId(targetId)
                            .params(params)
                            .channel(NotificationChannel.EMAIL)
                            .build()
            );
        } catch (Exception ignore) {
            // email channel failure should not block order workflow
        }
    }

    private void assignRunnerIfAvailable(Order o) {
        try {
            RunnerDTO runner = runnerClient.pickAnyAvailable();
            if (runner == null || runner.getId() == null) {
                return;
            }
            orderMapper.updateRunner(o.getId(), runner.getId());
            orderMapper.updateStatus(o.getId(), "ORDER_ASSIGNED");
            o.setRunnerId(runner.getId());
            o.setStatus("ORDER_ASSIGNED");
            try {
                refreshRunnerWorkloadStatus(runner.getId());
            } catch (Exception ignore) {
            }
        } catch (Exception ignore) {
            // keep order created if runner service is temporarily unavailable
        }
    }

    private void refreshRunnerWorkloadStatus(Long runnerId) {
        if (runnerId == null) {
            return;
        }
        int active = orderMapper.countActiveByRunnerId(runnerId);
        RunnerStatus target = active >= 2 ? RunnerStatus.BUSY : RunnerStatus.AVAILABLE;
        runnerClient.updateStatus(runnerId, UpdateStatusRequest.builder().status(target).build());
    }

    private CartSummaryDTO emptyCart() {
        return CartSummaryDTO.builder()
                .merchantId(null)
                .items(Collections.emptyList())
                .totalQuantity(0)
                .totalCents(0L)
                .build();
    }

    private LocalDate parseOrCurrentWeekStart(String weekStart) {
        try {
            if (weekStart != null && !weekStart.isBlank()) {
                LocalDate parsed = LocalDate.parse(weekStart);
                return parsed.with(java.time.DayOfWeek.MONDAY);
            }
        } catch (Exception ignore) {
        }
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    }

    private CartSnapshot readCart(Long userId) {
        List<CartItemRow> rows = cartItemMapper.listByUser(userId);
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        CartSnapshot snapshot = new CartSnapshot();
        for (CartItemRow row : rows) {
            if (row == null || row.getMenuItemId() == null || row.getQuantity() == null || row.getQuantity() <= 0) {
                continue;
            }
            if (snapshot.getMerchantId() == null) {
                snapshot.setMerchantId(row.getMerchantId());
            }
            snapshot.getQuantities().put(row.getMenuItemId(), row.getQuantity());
            snapshot.getUnitPrices().put(row.getMenuItemId(), row.getUnitPriceCents());
            snapshot.getMerchantByItem().put(row.getMenuItemId(), row.getMerchantId());
        }
        if (snapshot.getMerchantId() == null || snapshot.getQuantities().isEmpty()) {
            return null;
        }
        if (hasMultipleMerchants(snapshot)) {
            snapshot.setMerchantId(null);
        }
        return snapshot;
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

    private CartSummaryDTO toSummary(CartSnapshot snapshot) {
        if (snapshot == null || snapshot.getQuantities().isEmpty()) {
            return emptyCart();
        }
        Map<Long, Map<Long, MenuItemDTO>> menusByMerchant = new HashMap<>();
        for (Long merchantId : snapshot.getMerchantByItem().values()) {
            if (merchantId != null && !menusByMerchant.containsKey(merchantId)) {
                menusByMerchant.put(merchantId, menuMap(merchantId));
            }
        }
        List<CartItemDTO> items = new ArrayList<>();
        long totalCents = 0L;
        int totalQty = 0;

        for (Map.Entry<Long, Integer> entry : snapshot.getQuantities().entrySet()) {
            Long merchantId = snapshot.getMerchantByItem().get(entry.getKey());
            Map<Long, MenuItemDTO> menuMap = merchantId == null ? Collections.emptyMap() : menusByMerchant.getOrDefault(merchantId, Collections.emptyMap());
            MenuItemDTO menuItem = menuMap.get(entry.getKey());
            Long unitPrice = snapshot.getUnitPrices().get(entry.getKey());
            if (menuItem != null && Boolean.FALSE.equals(menuItem.getEnabled())) {
                continue;
            }
            int qty = entry.getValue();
            if (unitPrice == null) {
                unitPrice = menuItem == null ? 0L : menuItem.getPriceCents();
            }
            long subtotal = unitPrice * qty;
            totalCents += subtotal;
            totalQty += qty;
            items.add(CartItemDTO.builder()
                    .menuItemId(entry.getKey())
                    .merchantId(merchantId)
                    .name(menuItem == null ? "Item#" + entry.getKey() : menuItem.getName())
                    .unitPriceCents(unitPrice)
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

    private boolean hasMultipleMerchants(CartSnapshot snapshot) {
        Long first = null;
        for (Long merchantId : snapshot.getMerchantByItem().values()) {
            if (merchantId == null) continue;
            if (first == null) {
                first = merchantId;
                continue;
            }
            if (!first.equals(merchantId)) {
                return true;
            }
        }
        return false;
    }

    private List<OrderDetail> toOrderDetails(List<Order> orders) {
        List<OrderDetail> out = new ArrayList<>();
        if (orders == null) {
            return out;
        }
        for (Order o : orders) {
            out.add(buildOrderDetail(o, null));
        }
        return out;
    }

    private OrderDetail buildOrderDetail(Order order, String paymentStatus) {
        UserDTO user = getUserCached(order.getUserId());
        OrderDetail detail = new OrderDetail(order.getId(), user, order.getStatus(), order.getAmountCents(), paymentStatus);
        detail.setUserId(order.getUserId());
        detail.setMerchantId(order.getMerchantId());
        detail.setRunnerId(order.getRunnerId());
        if (user != null) {
            detail.setCustomerName(user.getUsername());
            detail.setCustomerPhone(user.getPhone());
            detail.setCustomerAddress(user.getAddress());
        }
        boolean canComplete = order.getRunnerId() != null
                && ("ORDER_ASSIGNED".equalsIgnoreCase(order.getStatus()) || "ORDER_PICKED_UP".equalsIgnoreCase(order.getStatus()));
        detail.setRunnerCanComplete(canComplete);
        return detail;
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
