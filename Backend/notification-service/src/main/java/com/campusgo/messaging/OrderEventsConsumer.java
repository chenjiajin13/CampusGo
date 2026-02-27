package com.campusgo.messaging;

import com.campusgo.dto.OrderEvent;
import com.campusgo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderEventsConsumer {

    private final KafkaDedup dedup;
    private final NotificationService notificationService; // 你项目里真正发通知的服务类

    @KafkaListener(topics = "order.events", groupId = "notification-service")
    public void on(OrderEvent event, Acknowledgment ack) {

        // 1) 幂等：处理过就直接 ack
        if (!dedup.firstTime(event.eventId())) {
            ack.acknowledge();
            return;
        }

        try {
            // 2) 处理业务（发通知）
            notificationService.handle(event);

            // 3) 成功才提交 offset
            ack.acknowledge();
        } catch (Exception e) {
            log.error("consume order event failed, eventId={}", event.eventId(), e);
            throw e; // 触发重试，最后进 order.events.DLT
        }
    }
}
