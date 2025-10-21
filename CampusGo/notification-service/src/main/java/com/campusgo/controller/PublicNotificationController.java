package com.campusgo.controller;


import com.campusgo.dto.NotificationCreateRequest;
import com.campusgo.dto.NotificationDTO;
import com.campusgo.enums.NotificationTargetType;
import com.campusgo.mapper.NotificationConverter;
import com.campusgo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class PublicNotificationController {


    private final NotificationService service;

    // user mailbox
    /** 前端：查询我的收件箱（示例以 user 为主） */
    @GetMapping("/inbox/user/{userId}")
    public List<NotificationDTO> inboxUser(@PathVariable("userId") Long userId) {
        return service.inbox(NotificationTargetType.USER, userId).stream().map(NotificationConverter::toDTO).collect(Collectors.toList());
    }

    // test
    /** 测试：手动发一条消息 */
    @PostMapping
    public NotificationDTO send(@RequestBody NotificationCreateRequest req) {
        return NotificationConverter.toDTO(service.send(req.getTargetType(), req.getTargetId(), req.getChannel(), req.getTitle(), req.getContent(), req.getData()));
    }

    // other target mailbox
    /** 其他目标收件箱（需要可继续扩展） */
    @GetMapping("/inbox/merchant/{merchantId}")
    public List<NotificationDTO> inboxMerchant(@PathVariable("merchantId") Long merchantId) {
        return service.inbox(NotificationTargetType.MERCHANT, merchantId).stream().map(NotificationConverter::toDTO).collect(Collectors.toList());
    }
}