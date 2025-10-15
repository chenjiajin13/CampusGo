package com.campusgo.service.impl;

import com.campusgo.client.NotificationClient;
import com.campusgo.client.UserClient;
import com.campusgo.domain.Order;
import com.campusgo.dto.OrderDetail;
import com.campusgo.dto.TemplateSendRequest;
import com.campusgo.dto.UserDTO;
import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationTargetType;
import com.campusgo.enums.TemplateKey;
import com.campusgo.service.OrderService;

import com.campusgo.store.InMemoryOrderStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

        private final UserClient userClient;
        private final NotificationClient notificationClient;
        private final AtomicLong orderIdGen = new AtomicLong(10000);


        @Override
        public OrderDetail getOrder(Long orderId) {
            UserDTO user = userClient.findById(orderId);
            return new OrderDetail(orderId, user, "CREATED");
        }

        @Override
        public OrderDetail createOrder(Long userId, Long merchantId, String address) {
            long orderId = orderIdGen.getAndIncrement();
            String status = "CREATED";

            UserDTO user = userClient.findById(userId);

            // 3) Trigger notification (user + merchant)
            Map<String, Object> params = Map.of("orderId", orderId);

            notificationClient.sendTemplate(
                    TemplateSendRequest.builder()
                            .template(TemplateKey.ORDER_PLACED)
                            .targetType(NotificationTargetType.USER)
                            .targetId(userId)
                            .params(params)
                            .channel(NotificationChannel.PUSH) // may omit
                            .build()
            );

            notificationClient.sendTemplate(
                    TemplateSendRequest.builder()
                            .template(TemplateKey.ORDER_PLACED)
                            .targetType(NotificationTargetType.MERCHANT)
                            .targetId(merchantId)
                            .params(params)
                            .channel(NotificationChannel.PUSH)
                            .build()
            );

            // 4) return  OrderDetail（orderId + user + status）
            return new OrderDetail(orderId, user, status);
        }
}

