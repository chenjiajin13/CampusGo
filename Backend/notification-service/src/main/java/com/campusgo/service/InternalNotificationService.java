package com.campusgo.service;


import com.campusgo.domain.Notification;
import com.campusgo.enums.*;


import java.util.Map;

// Internal service（order/payment/status-flow）
/** 面向微服务：下单/支付/状态流转时调用 */
public interface InternalNotificationService {
    Notification sendRaw(NotificationTargetType targetType, Long targetId, NotificationChannel channel, String title, String content, Map<String, Object> data);
    Notification sendTemplate(TemplateKey template, NotificationTargetType targetType, Long targetId, Map<String, Object> params, NotificationChannel channel);
}