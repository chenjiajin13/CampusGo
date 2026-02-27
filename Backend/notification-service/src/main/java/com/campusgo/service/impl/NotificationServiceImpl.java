package com.campusgo.service.impl;

import com.campusgo.domain.Notification;
import com.campusgo.dto.OrderEvent;
import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationStatus;
import com.campusgo.enums.NotificationTargetType;
import com.campusgo.mapper.NotificationMapper;
import com.campusgo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper mapper;

    @Override
    public List<Notification> inbox(NotificationTargetType targetType, Long targetId) {
        return mapper.listInbox(targetType, targetId);
    }

    @Override
    @Transactional
    public Notification send(NotificationTargetType targetType, Long targetId,
                             NotificationChannel channel, String title,
                             String content, Map<String, Object> data) {

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
    public void handle(OrderEvent e) {
        String msg = "Order " + e.orderId() + " status changed: " + e.type();
        System.out.println("[NOTIFY] " + msg);

        // 以后这里替换为：站内信 / push / email / websocket 等
    }

}
