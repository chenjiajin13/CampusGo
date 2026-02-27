package com.campusgo.messaging;

import com.campusgo.dto.OrderEvent;
import com.campusgo.dto.PaymentEvent;
import com.campusgo.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentEventsConsumer {

    private final KafkaDedup dedup;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderMapper orderMapper;

    @KafkaListener(topics = "payment.events", groupId = "order-service")
    public void on(PaymentEvent event, Acknowledgment ack) {

        if (!dedup.firstTime(event.eventId())) {
            ack.acknowledge();
            return;
        }

        try {
            handle(event);        // 写库 + afterCommit 发消息
            ack.acknowledge();    // 成功才提交 offset
        } catch (Exception e) {
            log.error("consume payment failed, eventId={}", event.eventId(), e);
            throw e; // 触发重试，最终进 payment.events.DLT
        }
    }

    @Transactional
    public void handle(PaymentEvent event) {
        var order = orderMapper.findById(event.orderId())
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + event.orderId()));

        if ("PAYMENT_SUCCEEDED".equals(event.type())) {
            order.setStatus("PAID");
        } else {
            order.setStatus("PAYMENT_FAILED");
        }
        orderMapper.updateStatus(order.getId(), order.getStatus());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                var oe = new OrderEvent(
                        UUID.randomUUID().toString(),
                        "PAYMENT_SUCCEED".equals(event.type()) ? "ORDER_PAID" : "ORDER_PAYMENT_FAILED",
                        order.getId(),
                        order.getUserId(),
                        order.getMerchantId(),
                        order.getAmountCents(),
                        System.currentTimeMillis()
                );
                orderEventPublisher.publish(oe);
            }
        });
    }
}
