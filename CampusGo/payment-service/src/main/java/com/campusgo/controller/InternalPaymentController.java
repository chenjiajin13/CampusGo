package com.campusgo.controller;


import com.campusgo.client.NotificationClient;
import com.campusgo.domain.Payment;
import com.campusgo.dto.*;
import com.campusgo.enums.*;
import com.campusgo.mapper.PaymentConverter;
import com.campusgo.service.InternalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/internal/payments")
@RequiredArgsConstructor
public class InternalPaymentController {


    private final InternalPaymentService internal;
    private final NotificationClient notificationClient;


    /** for order-service Call: Initiate payment record */
    @PostMapping("/initiate")
    public PaymentDTO initiate(@RequestBody PaymentCreateRequest req) {
        return PaymentConverter.toDTO(internal.initiate(req.getOrderId(), req.getUserId(), req.getMerchantId(), req.getAmountCents(), req.getCurrency(), req.getMethod()));
    }


    /** order-service Call: Query payment records based on order number */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getByOrder(@PathVariable("orderId") Long orderId) {
        return internal.getByOrderId(orderId).map(PaymentConverter::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    /** mock:failed */
    @PostMapping("/{id}/simulate/fail")
    public PaymentDTO simulateFail(@PathVariable("id") Long id) {
        return PaymentConverter.toDTO(internal.setStatus(id, PaymentStatus.FAILED));
    }

    /** mock:success */
    @PostMapping("/{id}/simulate/success")
    public PaymentDTO simulateSuccess(@PathVariable("id") Long id) {
        Payment payment = internal.setStatus(id, PaymentStatus.SUCCESS);
        PaymentDTO dto = PaymentConverter.toDTO(payment);

        // send successful payment notification
        Map<String, Object> params = Map.of("orderId", payment.getOrderId());
        notificationClient.sendTemplate(
                TemplateSendRequest.builder()
                        .template(TemplateKey.PAYMENT_SUCCESS)
                        .targetType(NotificationTargetType.USER)
                        .targetId(payment.getUserId())
                        .params(params)
                        .channel(NotificationChannel.PUSH)
                        .build()
        );

        return dto;
    }
}