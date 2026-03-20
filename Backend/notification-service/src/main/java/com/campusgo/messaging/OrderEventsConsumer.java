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
    private final NotificationService notificationService; // Service that performs actual notification dispatch.

    @KafkaListener(topics = "order.events", groupId = "notification-service")
    public void on(OrderEvent event, Acknowledgment ack) {

        // 1) Idempotency: if already processed, acknowledge and return.
        if (!dedup.firstTime(event.eventId())) {
            ack.acknowledge();
            return;
        }

        try {
            // 2) Handle business logic (send notifications).
            notificationService.handle(event);

            // 3) Commit offset only after successful processing.
            ack.acknowledge();
        } catch (Exception e) {
            log.error("consume order event failed, eventId={}", event.eventId(), e);
            throw e; // Trigger retry; eventually route to order.events.DLT.
        }
    }
}
