package com.campusgo.service;


import com.campusgo.domain.Notification;
import com.campusgo.dto.OrderEvent;
import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationTargetType;


import java.util.List;
import java.util.Map;


/** for frontend use */
public interface NotificationService {
    List<Notification> inbox(NotificationTargetType targetType, Long targetId);
    Notification send(NotificationTargetType targetType, Long targetId, NotificationChannel channel, String title, String content, Map<String, Object> data);
    void handle(OrderEvent event);
}