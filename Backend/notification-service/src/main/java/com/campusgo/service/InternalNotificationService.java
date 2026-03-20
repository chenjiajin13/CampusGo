package com.campusgo.service;


import com.campusgo.domain.Notification;
import com.campusgo.enums.*;


import java.util.Map;

// Internal service for order/payment/status-flow notifications.
/** Service API used by microservices during order/payment/status transitions. */
public interface InternalNotificationService {
    Notification sendRaw(NotificationTargetType targetType, Long targetId, NotificationChannel channel, String title, String content, Map<String, Object> data);
    Notification sendTemplate(TemplateKey template, NotificationTargetType targetType, Long targetId, Map<String, Object> params, NotificationChannel channel);
}
