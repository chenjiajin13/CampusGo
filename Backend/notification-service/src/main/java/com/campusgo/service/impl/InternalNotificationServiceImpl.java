package com.campusgo.service.impl;

import com.campusgo.domain.Notification;
import com.campusgo.enums.*;
import com.campusgo.mapper.NotificationMapper;
import com.campusgo.service.InternalNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InternalNotificationServiceImpl implements InternalNotificationService {

    private final NotificationMapper mapper;

    @Override
    @Transactional
    public Notification sendRaw(NotificationTargetType targetType,
                                Long targetId,
                                NotificationChannel channel,
                                String title,
                                String content,
                                Map<String, Object> data) {

        Notification n = Notification.builder()
                .targetType(targetType)
                .targetId(targetId)
                .channel(channel == null ? NotificationChannel.PUSH : channel)
                .title(title)
                .content(content)
                .data(data == null ? Map.of() : data)
                .status(NotificationStatus.PENDING)
                .build();

        mapper.insert(n);
        mapper.markSent(n.getId());
        return mapper.findById(n.getId()).orElse(n);
    }

    @Override
    @Transactional
    public Notification sendTemplate(TemplateKey template,
                                     NotificationTargetType targetType,
                                     Long targetId,
                                     Map<String, Object> params,
                                     NotificationChannel channel) {

        Map<String, Object> p = (params == null) ? new HashMap<>() : new HashMap<>(params);
        String title; String content;
        switch (template) {
            case ORDER_PLACED -> { title = "Order Placed"; content = "Your order #" + p.get("orderId") + " has been placed."; }
            case ORDER_ASSIGNED -> { title = "Order Assigned"; content = "Runner #" + p.get("runnerId") + " assigned."; }
            case ORDER_PICKED_UP -> { title = "Picked Up"; content = "Order #" + p.get("orderId") + " picked up."; }
            case ORDER_DELIVERED -> { title = "Delivered"; content = "Order #" + p.get("orderId") + " delivered."; }
            case PAYMENT_SUCCESS -> { title = "Payment Success"; content = "Payment successful for order #" + p.get("orderId"); }
            case REFUND_SUCCESS -> { title = "Refund Success"; content = "Refund processed for order #" + p.get("orderId"); }
            case MERCHANT_STATUS_CHANGED -> { title = "Merchant Status"; content = "Merchant status changed."; }
            default -> { title = "Notification"; content = p.toString(); }
        }
        return sendRaw(targetType, targetId, channel, title, content, p);
    }
}
