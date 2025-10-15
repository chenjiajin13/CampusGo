package com.campusgo.service.impl;

import com.campusgo.domain.Notification;
import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationTargetType;
import com.campusgo.service.NotificationService;
import com.campusgo.store.InMemoryNotificationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final InMemoryNotificationStore store;

    @Override public List<Notification> inbox(NotificationTargetType targetType, Long targetId) { return store.listInbox(targetType, targetId); }
    @Override public Notification send(NotificationTargetType targetType, Long targetId, NotificationChannel channel, String title, String content, Map<String, Object> data) {
        Notification n = store.create(targetType, targetId, channel, title, content, data);
        return store.markSent(n.getId());
    }
}