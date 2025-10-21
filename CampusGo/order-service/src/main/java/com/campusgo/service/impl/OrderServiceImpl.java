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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    @Override
    public OrderDetail getOrder(Long orderId) {
        Order o = orderMapper.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        UserDTO user = userClient.findById(o.getUserId());
        return new OrderDetail(o.getId(), user, o.getStatus());
    }

    @Override
    @Transactional
    public OrderDetail createOrder(Long userId, Long merchantId, String address) {
        // 1) build an order
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

        // 2) find user
        UserDTO user = userClient.findById(userId);

        // 3) send notification
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

        // 4) return the combination of OrderDetail and UserDTO
        return new OrderDetail(o.getId(), user, o.getStatus());
    }


    public List<Order> listAll() {
        return orderMapper.listAll();
    }


    @Transactional
    public void updateStatus(Long orderId, String status) {
        orderMapper.updateStatus(orderId, status);
    }
}
