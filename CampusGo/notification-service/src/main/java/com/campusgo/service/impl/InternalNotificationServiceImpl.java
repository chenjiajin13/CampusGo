package com.campusgo.service.impl;


import com.campusgo.domain.Notification;
import com.campusgo.enums.*;
import com.campusgo.service.InternalNotificationService;
import com.campusgo.store.InMemoryNotificationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class InternalNotificationServiceImpl implements InternalNotificationService {
    private final InMemoryNotificationStore store;


    @Override public Notification sendRaw(NotificationTargetType targetType, Long targetId, NotificationChannel channel, String title, String content, Map<String, Object> data) {
        Notification n = store.create(targetType, targetId, channel, title, content, data == null ? Map.of() : data);
        return store.markSent(n.getId());
    }


    @Override public Notification sendTemplate(TemplateKey template, NotificationTargetType targetType, Long targetId, Map<String, Object> params, NotificationChannel channel) {
        Map<String,Object> p = params == null ? new HashMap<>() : new HashMap<>(params);
        String title; String content;
        switch (template) {
            case ORDER_PLACED -> { title = "Order Placed"; content = "Your order #" + p.get("orderId") + " has been placed."; }
            case ORDER_ASSIGNED -> { title = "Order Assigned"; content = "Runner #" + p.get("runnerId") + " is assigned for order #" + p.get("orderId") + "."; }
            case ORDER_PICKED_UP -> { title = "Picked Up"; content = "Order #" + p.get("orderId") + " is picked up."; }
            case ORDER_DELIVERED -> { title = "Delivered"; content = "Order #" + p.get("orderId") + " delivered."; }
            case PAYMENT_SUCCESS -> { title = "Payment Success"; content = "Payment successful for order #" + p.get("orderId") + "."; }
            case REFUND_SUCCESS -> { title = "Refund Success"; content = "Refund processed for order #" + p.get("orderId") + "."; }
            case MERCHANT_STATUS_CHANGED -> { title = "Merchant Status"; content = "Merchant status changed to " + p.get("status") + "."; }
            default -> { title = "Notification"; content = p.toString(); }
        }
        Notification n = store.create(targetType, targetId, channel, title, content, p);
        return store.markSent(n.getId());
    }
}