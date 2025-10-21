package com.campusgo.controller;


import com.campusgo.dto.NotificationDTO;
import com.campusgo.dto.TemplateSendRequest;
import com.campusgo.mapper.NotificationConverter;
import com.campusgo.service.InternalNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class InternalNotificationController {


    private final InternalNotificationService internal;

    // Microservice template sending (order/payment/delivery status, etc.)
    /** 微服务模板发送（订单/支付/配送状态等） */
    @PostMapping("/send-template")
    public NotificationDTO sendTemplate(@RequestBody TemplateSendRequest req) {
        return NotificationConverter.toDTO(internal.sendTemplate(req.getTemplate(), req.getTargetType(), req.getTargetId(), req.getParams(), req.getChannel()));
    }

    /** The microservice directly sends a text notification */
    /** 微服务直接发送一条文本通知 */
    @PostMapping("/send-raw")
    public NotificationDTO sendRaw(@RequestBody com.campusgo.dto.NotificationCreateRequest req) {
        return NotificationConverter.toDTO(internal.sendRaw(req.getTargetType(), req.getTargetId(), req.getChannel(), req.getTitle(), req.getContent(), req.getData()));
    }
}